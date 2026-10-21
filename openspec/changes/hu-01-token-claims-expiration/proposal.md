# HU-01: Token firmado con expiracion y claims minimos

## Issue

Trabajo comprometido: [GitHub Issue #10](https://github.com/user-bcp-student47/workshop-auth-backend/issues/10).

## Why

El login emite tokens que representan una sesion autenticada. Claims minimos verificables y una expiracion coherente permiten identificar al sujeto y al cliente, y detectar tokens vencidos sin depender de supuestos del consumidor. La configuracion del secreto y la redaccion de logs reducen el riesgo de exponer credenciales o material de firma.

## Scope

- Exigir `sub`, `client`, `iat` y `exp` en los tokens de login exitoso.
- Validar el campo `client` recibido en el body y reflejar el valor validado en el token.
- Devolver `expiresIn` coherente con `exp - now`, con una tolerancia documentada.
- Cubrir login valido, credenciales invalidas y token expirado con pruebas.
- Evitar registrar tokens, secretos y otros valores sensibles.
- Asegurar que un secreto por defecto sea exclusivo del sandbox y este documentado como tal, o que no se use fuera de ese entorno.
- Documentar la decision entre conservar HMAC o adoptar una libreria JWT y su justificacion, sin preseleccionar aqui ninguna alternativa.

## Out of scope

- Elegir ahora HMAC o una libreria JWT, definir algoritmos, dependencias, formato interno o arquitectura de validacion.
- Cambiar el endpoint, los campos existentes del request o la forma de manejar errores HTTP.
- Rediseñar el almacenamiento de sesion del frontend, agregar refresh/revocacion, modificar la API de tareas o ampliar el esquema de `user`.
- Especificar codigos HTTP o cuerpos de error que `docs/ENDPOINTS.md` deja como preguntas abiertas.
- Implementar cambios funcionales o crear `tasks.md` en esta propuesta.

## Behavior to preserve

`docs/ENDPOINTS.md` es el baseline del consumidor frontend: se conserva `POST /auth/login`, el body JSON con `username`, `password` y `client` (actualmente `angular-workshop`), y la respuesta JSON directa, sin un wrapper, con el campo `token` que el frontend exige para considerar exitoso el login. Se conserva el campo y tratamiento actual de `user` sin imponerle un esquema nuevo. No se fija un status HTTP exitoso concreto ni un formato de error: el frontend acepta 2xx con token y mantiene su manejo publico actual de fallos y respuestas sin token.

El Issue #10 cambia el contrato de respuesta al requerir `expiresIn`, que el baseline frontend no consume hoy. Se agrega como campo superior sin retirar ni renombrar `token` o `user`; es una ampliacion compatible con el consumidor documentado. `client` ya forma parte del request actual, por lo que validarlo y reflejarlo en el token no requiere cambiar la llamada del frontend.

## Capabilities

- `auth-login-token`: claims, validacion de `client`, expiracion y compatibilidad del contrato de login.
- `auth-token-protection`: manejo de `AUTH_TOKEN_SECRET`, logs redactados y registro de la decision HMAC/JWT.

## Impact

El cambio afecta el contrato de `POST /auth/login`, la configuracion/documentacion de `AUTH_TOKEN_SECRET`, las pruebas de autenticacion y la evidencia de logs. No prescribe la implementacion tecnica. Los consumidores que ya usan `token` y `user` deben continuar funcionando; `expiresIn` se incorpora como dato adicional.