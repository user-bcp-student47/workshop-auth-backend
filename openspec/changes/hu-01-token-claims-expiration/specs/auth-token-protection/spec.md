## ADDED Requirements

### Requirement: El secreto de firma no tiene un fallback fuera del sandbox

El servicio SHALL evitar el uso de un secreto por defecto fuera del sandbox. Si se conserva un valor por defecto para el laboratorio, la documentacion SHALL identificarlo explicitamente como exclusivo de ese entorno y explicar la configuracion requerida fuera de el. No se incluiran secretos reales en la documentacion.

#### Scenario: Uso fuera del sandbox

- GIVEN el servicio se configura para un entorno que no es el sandbox
- WHEN no se proporciona `AUTH_TOKEN_SECRET`
- THEN el servicio no firma tokens usando silenciosamente un secreto por defecto del laboratorio

#### Scenario: Valor por defecto documentado para laboratorio

- GIVEN se conserva un valor por defecto para uso de laboratorio
- WHEN se consulta la documentacion de configuracion
- THEN el valor se identifica como exclusivo del sandbox
- AND se explica que fuera del sandbox se requiere la configuracion correspondiente de `AUTH_TOKEN_SECRET`
- AND no se revela un secreto real

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

### Requirement: La decision HMAC o JWT queda documentada

La documentacion del cambio SHALL registrar si se conserva HMAC o se adopta una libreria JWT y la justificacion de esa decision. Este requisito no selecciona una alternativa ni prescribe detalles de implementacion en esta propuesta.

#### Scenario: Decision tecnica registrada

- GIVEN se completa el cambio de autenticacion
- WHEN se revisa la evidencia/documentacion asociada
- THEN consta la alternativa elegida entre HMAC y una libreria JWT y su justificacion
- AND la decision no expone secretos