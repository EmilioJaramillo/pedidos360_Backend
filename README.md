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

Las DLQ reciben los mensajes rechazados a través del exchange `suspel.dlx` (direct). Los nombres se definen en el `application.yml` de cada servicio, bajo `suspel.rabbit`.

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

## Endpoints de productos-service

| Método | Ruta |
|---|---|
| GET, POST | `/api/productos` |
| GET, PUT, DELETE | `/api/productos/{id}` |
