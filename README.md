# Workshop Auth Backend — Java Quarkus

> **Laboratorio intencionalmente vulnerable.** Ejecutar únicamente en local, repositorios privados y ambientes aislados. No desplegar en Internet, CERT o PROD.

Backend Quarkus que autentica usuarios de laboratorio almacenados en SQLite y entrega un token consumido por el frontend Angular y la API Python.

## Ejecución

Requisitos: JDK 21 y Maven 3.9+.

La firma de tokens usa JWT HS256. Define `AUTH_TOKEN_SECRET` con al menos 32 bytes UTF-8 antes de iniciar el servicio; no existe un valor por defecto. Para una terminal PowerShell local, usa un valor ficticio solo para el laboratorio:

```powershell
$env:AUTH_TOKEN_SECRET = "replace-with-a-local-random-value-of-32-bytes-or-more"
mvn quarkus:dev
```

```bash
export AUTH_TOKEN_SECRET="replace-with-a-local-random-value-of-32-bytes-or-more"
mvn quarkus:dev
```

Servicio: `http://localhost:8080`

Credenciales locales:

- `student` / `Workshop123!`
- `trainer` / `Trainer123!`

## Endpoints

- `POST /auth/login`
- `GET /q/health`

El login acepta `client: angular-workshop` y devuelve `token`, `user` y `expiresIn` en el objeto JSON directo. El token incluye `sub`, `role`, `client`, `iat` y `exp`. Este backend emite el claim de expiración, pero no valida tokens entrantes; el consumidor debe verificar la firma y rechazar tokens expirados.

## Propósito de la rama `main`

La rama contiene deuda técnica y de seguridad deliberada: SQL concatenado, contraseñas en texto plano, token reversible, secreto hardcodeado, CORS permisivo, método largo, manejo genérico de excepciones y dependencias con CVEs.
