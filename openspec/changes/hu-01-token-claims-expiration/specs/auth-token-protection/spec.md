## ADDED Requirements

### Requirement: El secreto de firma no tiene un fallback fuera del sandbox

El servicio SHALL obtener el secreto de firma de configuracion por entorno y no SHALL incluir un secreto hardcodeado ni fallback por defecto. Las pruebas SHALL usar un valor ficticio; no se incluiran secretos reales en la documentacion.

#### Scenario: Falta el secreto de firma

- GIVEN el servicio se inicia en cualquier entorno
- WHEN no se proporciona `AUTH_TOKEN_SECRET`
- THEN el servicio no firma tokens con un valor por defecto

### Requirement: Los logs no exponen datos sensibles de autenticacion

El servicio SHALL evitar registrar tokens, el valor de `AUTH_TOKEN_SECRET`, contrasenas u otros valores sensibles recibidos o generados durante autenticacion.

#### Scenario: Login exitoso sin secretos en logs

- GIVEN se procesa un login exitoso
- WHEN se revisan los logs producidos por el flujo
- THEN no aparecen el token emitido, el secreto de firma ni las credenciales

#### Scenario: Login fallido sin secretos en logs

- GIVEN se procesa un login fallido
- WHEN se revisan los logs producidos por el flujo
- THEN no aparecen el token, el secreto de firma, las credenciales ni valores sensibles del request

### Requirement: El JWT se firma con una libreria mantenida

El servicio SHALL usar una libreria JWT mantenida y compatible con Quarkus, con algoritmo HS256. La documentacion SHALL justificar la decision y especificar que `AUTH_TOKEN_SECRET` debe tener al menos 32 bytes UTF-8. El token SHALL verificarse en los consumidores; esta capability no agrega validacion de tokens entrantes en este backend.

#### Scenario: Firma y manejo de clave documentados

- GIVEN se completa la emision de tokens
- WHEN se revisa la evidencia/documentacion asociada
- THEN consta el uso de la libreria JWT y HS256 con su justificacion
- AND se documenta el requisito de longitud de la clave sin revelar su valor