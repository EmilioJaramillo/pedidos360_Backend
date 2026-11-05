# Pedidos360 / Suspel Backend (Spring Boot)

API REST de productos Suspel (`/api/productos`). Actúa como **OAuth2 Resource Server**: valida el Access Token JWT emitido por Amazon Cognito (firma, issuer) y exige el scope `suspel-api/productos-access`. Es la segunda capa de validación (Defense in Depth); la primera es el Cognito Authorizer de API Gateway.

## Requisitos

- Java 17 o superior (se usa el Maven Wrapper incluido)

## Ejecutar en local

```bash
./mvnw spring-boot:run
```

Escucha en el puerto 8080. Sin token responde 401; con un token sin el scope requerido, 403.

## Configuración (`src/main/resources/application.properties`)

| Propiedad | Uso |
|---|---|
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | Issuer del User Pool de Cognito |
| `suspel.security.scope` | Scope exigido en `/api/productos/**` |
| `suspel.cors.allowed-origins` | Orígenes permitidos (frontend Angular) |

Base de datos: H2 en memoria, se reinicia con los datos de `data.sql` en cada arranque.

## Despliegue en EC2 (vía S3)

```bash
# en el equipo de desarrollo
./mvnw clean package -DskipTests
# subir target/suspel-backend.jar al bucket S3 (consola web)

# en la instancia EC2
aws s3 cp s3://<BUCKET>/suspel-backend.jar ./suspel-backend.jar
sudo systemctl restart suspel
sudo journalctl -u suspel -n 5 --no-pager
```

La instancia usa el IAM Role asociado para leer el bucket (sin credenciales estáticas). El Security Group debe permitir el puerto 8080.

El backend corre como servicio `systemd` (`/etc/systemd/system/suspel.service`, ejecuta `java -jar /home/ec2-user/suspel-backend.jar` con `Restart=on-failure`) y arranca solo al iniciar la instancia.

## Endpoints

| Método | Ruta |
|---|---|
| GET, POST | `/api/productos` |
| GET, PUT, DELETE | `/api/productos/{id}` |
