# HU-01: Token firmado con expiracion y claims minimos

## Issue

Trabajo comprometido: [GitHub Issue #10](https://github.com/user-bcp-student47/workshop-auth-backend/issues/10).

## Why

El login emite tokens que representan una sesion autenticada. Claims minimos verificables y una expiracion coherente permiten identificar al sujeto y al cliente; el servicio consumidor debe validar la firma y rechazar tokens vencidos. La configuracion del secreto y la redaccion de logs reducen el riesgo de exponer credenciales o material de firma.

## Scope

- Exigir `sub`, `client`, `iat` y `exp` en los tokens de login exitoso.
- Validar el campo `client` recibido en el body y reflejar el valor validado en el token.
- Devolver `expiresIn` coherente con `exp - now`, con una tolerancia documentada.
- Cubrir login valido, credenciales invalidas, cliente rechazado y claims de expiracion emitidos con pruebas. La validacion y el rechazo efectivo de tokens expirados pertenecen al consumidor y no se implementan aqui.
- Evitar registrar tokens, secretos y otros valores sensibles.
- Obtener el secreto de firma de configuracion por entorno, sin secreto por defecto.
- Usar una libreria JWT mantenida y compatible con Quarkus; documentar la eleccion y su justificacion.

## Out of scope

- Agregar un endpoint, filtro u otra frontera de validacion de tokens en este repositorio; el consumidor debe aplicar la validacion de expiracion.
- Modificar otros repositorios o afirmar compatibilidad con consumidores no verificados.
- Cambiar el endpoint, los campos existentes del request o la forma de manejar errores HTTP.
- Rediseñar el almacenamiento de sesion del frontend, agregar refresh/revocacion, modificar la API de tareas o ampliar el esquema de `user`.
- Especificar codigos HTTP o cuerpos de error que `docs/ENDPOINTS.md` deja como preguntas abiertas.
- Implementar cambios funcionales o crear `tasks.md` en esta propuesta.

## Behavior to preserve

`docs/ENDPOINTS.md` es el baseline del consumidor frontend: se conserva `POST /auth/login`, el body JSON con `username`, `password` y `client` (actualmente `angular-workshop`), y la respuesta JSON directa, sin un wrapper, con el campo `token` que el frontend exige para considerar exitoso el login. Se conserva el campo y tratamiento actual de `user` sin imponerle un esquema nuevo. No se fija un status HTTP exitoso concreto ni un formato de error: el frontend acepta 2xx con token y mantiene su manejo publico actual de fallos y respuestas sin token.

El Issue #10 cambia el contrato de respuesta al requerir `expiresIn`, que el baseline frontend no consume hoy. Se agrega como campo superior sin retirar ni renombrar `token` o `user`; es una ampliacion compatible con el consumidor documentado. `client` ya forma parte del request actual, por lo que validarlo y reflejarlo en el token no requiere cambiar la llamada del frontend.

## Capabilities

- `auth-login-token`: claims emitidos, allowlist de `client`, expiracion y compatibilidad del contrato de login.
- `auth-token-protection`: manejo de `AUTH_TOKEN_SECRET`, logs redactados y decision de libreria JWT.

## Impact

El cambio afecta el contrato de `POST /auth/login`, la configuracion/documentacion de `AUTH_TOKEN_SECRET`, las pruebas de autenticacion y la evidencia de logs. Se cambia el formato opaco heredado por un JWT HS256; `expiresIn` se incorpora como dato adicional. La compatibilidad de validadores no inspeccionados debe confirmarse antes del despliegue.