# Stockman

API de autenticación con Spring Boot, JPA, JWT y PostgreSQL.

## Requisitos
- Java 21
- Docker + Docker Compose (opcional, para levantar PostgreSQL)

## Instalación
Clonar el repositorio y entrar al proyecto.

### Base de datos (Docker)
```bash
docker compose up -d
```

### Configuración
Verificar credenciales y URL en `src/main/resources/application.properties`.

Valores por defecto:
- `jdbc:postgresql://localhost:5432/stockman_db`
- usuario: `marckux`
- password: `secret`

## Ejecución
Con Maven Wrapper:
```bash
./mvnw spring-boot:run
```

## Endpoints
Base URL local: `http://localhost:8080`

### Endpoint de ejemplo de acceso público
```bash
curl -s http://localhost:8080/api/public/check-health
```

### Login
```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"super_admin@example.mail","password":"super"}'
```

La respuesta incluye `token`. Usarlo como Bearer para rutas privadas.

### Ruta privada (requiere token)
```bash
TOKEN="pega_el_token_aqui"
curl -s http://localhost:8080/api/private/check-health \
  -H "Authorization: Bearer $TOKEN"
```

### Registrar usuario (solo ADMIN o SUPER_ADMIN)
```bash
TOKEN="pega_el_token_aqui"
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"email":"user@example.com","password":"StrongPass1!","name":"User"}'
```

### Cambiar password (solo usuario activo)
```bash
TOKEN="pega_el_token_aqui"
curl -s -X PATCH http://localhost:8080/api/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"currentPassword":"StrongPass1!","newPassword":"StrongPass2!"}'
```

### Listar usuarios (solo ADMIN o SUPER_ADMIN)
```bash
TOKEN="pega_el_token_aqui"
curl -s http://localhost:8080/api/auth/users \
  -H "Authorization: Bearer $TOKEN"
```

### Buscar usuario por id (solo ADMIN o SUPER_ADMIN)
```bash
TOKEN="pega_el_token_aqui"
USER_ID="uuid_del_usuario"
curl -s http://localhost:8080/api/auth/users/$USER_ID \
  -H "Authorization: Bearer $TOKEN"
```

## Tests
```bash
./mvnw test
```

## E2E
El runner está en `e2etests/api-test.zsh`. Ejecuta archivos `.yml` en orden numérico.

Requisitos: `curl`, `jq`, `yq`.

Uso básico:
```bash
./e2etests/api-test.zsh e2etests/tests/*.yml
```

Variables y sesión:
- El token Bearer se guarda automáticamente si la respuesta trae `token` o `accessToken`.
- `logout: true` limpia el token actual.
- `store` permite guardar campos del JSON para usar en tests posteriores.
- Puedes referenciar variables con `{{var}}` en `endpoint` y `body`.

Ejemplo de `store`:
```yml
store:
  user_id: id
```

## Notas
En desarrollo se usa `spring.jpa.hibernate.ddl-auto=update`. En producción conviene `validate`.

Al iniciar, si no hay usuarios, se crea un `SUPER_ADMIN` por defecto. Email: `super_admin@example.mail`. Password: `super`.
