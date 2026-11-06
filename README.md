# Pedidos360 / Suspel Backend

Backend de Pedidos360 compuesto por tres microservicios Spring Boot 3.5 (Java 17) y un clúster RabbitMQ de dos nodos. Todos validan el access token JWT emitido por Amazon Cognito y exigen el scope `suspel-api/productos-access`.

| Servicio | Puerto | Rol |
|---|---|---|
| `productos-service` | 8080 | CRUD de productos Suspel (`/api/productos`). Publica eventos de inventario. |
| `procesador-service` | 8081 | Consumidores RabbitMQ: auditoría, notificaciones y documentos. |
| `rabbit-admin-service` | 8082 | API REST para administrar colas, exchanges y bindings (`/api/admin`). |
| `rabbit1`, `rabbit2` | 5672/15672, 5673/15673 | Clúster RabbitMQ (management UI en 15672 y 15673). |

## Mensajería

| Exchange | Tipo | Routing key | Cola | DLQ |
|---|---|---|---|---|
| `suspel.inventario.topic` | topic | `producto.#` | `q.auditoria` | `q.auditoria.dlq` |
| `suspel.operaciones.direct` | direct | `stock.bajo` | `q.notificaciones` | `q.notificaciones.dlq` |
| `suspel.operaciones.direct` | direct | `documento.ficha` | `q.documentos` | `q.documentos.dlq` |
| `suspel.emergencias.topic` | topic | `emergencia.#` | `q.emergencias` (prioridad 0-10) | `q.emergencias.dlq` |

Las DLQ reciben los mensajes rechazados a través del exchange `suspel.dlx` (direct). Los nombres se definen en el `application.yml` de cada servicio, bajo `suspel.rabbit`.

Confirmación de mensajes en `procesador-service` (`acknowledge-mode: manual`, clase `ManejadorMensajes`):

| Resultado | Acción |
|---|---|
| Procesado correctamente | `basicAck` |
| JSON inválido, campos faltantes o clase IMO desconocida | `basicNack(requeue=false)` → DLQ |
| Error recuperable (canal de notificaciones o BD no disponible) | Se republica con `x-intentos + 1`; al superar `max-intentos` (3) → DLQ |
| Error no controlado | `basicNack(requeue=false)` → DLQ |

Cada envío a DLQ queda registrado en el log con la cola, el `message_id` y el motivo. Para simular un error recuperable: `CANAL_NOTIFICACIONES=false`.

## Ejecutar

Requisitos: Docker con Compose y Java 17 o superior.

```bash
cp .env.example .env          # definir usuario, password y cookie de RabbitMQ
docker compose up -d --build  # clúster RabbitMQ + 3 microservicios
```

Solo RabbitMQ (para ejecutar los servicios desde el IDE):

```bash
docker compose up -d rabbit1 rabbit2
cd productos-service && ./mvnw spring-boot:run
```

Verificar el clúster:

```bash
docker compose exec rabbit1 rabbitmqctl cluster_status
```

## Configuración común

| Propiedad / variable | Uso |
|---|---|
| `RABBIT_ADDRESSES` | Nodos del clúster (por defecto `localhost:5672,localhost:5673`) |
| `RABBIT_USER`, `RABBIT_PASS` | Credenciales de RabbitMQ |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | Issuer del User Pool de Cognito |
| `suspel.security.scope` | Scope exigido por la API |
| `suspel.cors.allowed-origins` | Orígenes permitidos (frontend Angular) |

Base de datos: H2 en memoria en `productos-service` (datos iniciales en `data.sql`) y en `procesador-service`.

## Despliegue en EC2

La instancia (t3.medium o superior) necesita Docker y Git. El Security Group debe abrir 8080 (API Gateway) y, para la administración, 8081, 8082, 15672 y 15673.

```bash
git clone https://github.com/EmilioJaramillo/pedidos360_Backend.git
cd pedidos360_Backend
cp .env.example .env && nano .env
docker compose up -d --build
docker compose ps
```

## Endpoints

| Servicio | Método | Ruta |
|---|---|---|
| productos-service | GET, POST | `/api/productos` |
| productos-service | GET, PUT, DELETE | `/api/productos/{id}` |
| productos-service | POST | `/api/emergencias` |
| procesador-service | GET | `/api/actividad/auditoria` |
| procesador-service | GET | `/api/actividad/notificaciones` |
| procesador-service | GET | `/api/actividad/fichas` |
| procesador-service | GET | `/api/actividad/emergencias` |
| rabbit-admin-service | GET, POST | `/api/admin/queues` |
| rabbit-admin-service | GET, DELETE | `/api/admin/queues/{nombre}` |
| rabbit-admin-service | DELETE | `/api/admin/queues/{nombre}/messages` (purga) |
| rabbit-admin-service | GET, POST | `/api/admin/exchanges` |
| rabbit-admin-service | DELETE | `/api/admin/exchanges/{nombre}` |
| rabbit-admin-service | GET, POST | `/api/admin/bindings` |
| rabbit-admin-service | DELETE | `/api/admin/bindings?exchange=&cola=&routingKey=` |

Ejemplos de `rabbit-admin-service`:

```json
POST /api/admin/queues     {"nombre": "q.reportes", "conDlq": true, "ttlMs": 60000}
POST /api/admin/exchanges  {"nombre": "suspel.reportes.topic", "tipo": "TOPIC"}
POST /api/admin/bindings   {"exchange": "suspel.reportes.topic", "cola": "q.reportes", "routingKey": "reporte.#"}
```

Respuestas: `201` creado, `204` eliminado, `400` datos inválidos (con detalle por campo), `404` no existe, `409` ya existe o es un recurso del sistema (`suspel.rabbit-admin.recursos-protegidos`), `503` RabbitMQ no disponible.

## Avisos de emergencia

`POST /api/emergencias {"bodega": "Bodega 5", "tipo": "INCENDIO", "detalle": "..."}` publica en `suspel.emergencias.topic` un aviso con las sustancias de la bodega. La cola `q.emergencias` tiene `x-max-priority: 10`: un incendio (9) se despacha antes que una fuga de gas (7) o un derrame (5) aunque haya llegado después. `procesador-service` registra el aviso con folio `EM-000001` y lo deja en el log como despacho a Bomberos (simulado). Si RabbitMQ no recibe el aviso, la API responde `503` en lugar de perderlo en silencio.
