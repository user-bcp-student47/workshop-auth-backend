# Tasks: HU-01 Token firmado con expiracion y claims minimos

Issue de producto: [GitHub Issue #10](https://github.com/user-bcp-student47/workshop-auth-backend/issues/10).

## 1. Caracterizar el comportamiento actual

- [x] 1.1 Ampliar `AuthResourceTest` para fijar el request documentado (`username`, `password`, `client`), el login exitoso, la respuesta JSON directa y los campos `token` y `user` consumidos por el frontend.
- [x] 1.2 Caracterizar las respuestas actuales para credenciales incorrectas y `username`/`password` ausentes o vacios, sin ampliar el contrato de error.
- [x] 1.3 Registrar mediante pruebas de caracterizacion el comportamiento actual de `client`, el formato heredado del token, `expiresIn` y `debug`; marcar como transitorios los detalles que cambian intencionalmente y no incluir el secreto literal en fixtures.
- [x] 1.4 Capturar logs del flujo de autenticacion con valores sentinel para establecer si errores de consulta exponen credenciales, SQL, tokens o secretos.

## 2. Resolver gates del diseno

- [x] 2.1 Decidir Auth0 `java-jwt` con HS256; documentar rationale, requisito de clave y compatibilidad no verificada. El Base64 heredado no es una firma.
- [x] 2.2 Permitir unicamente `angular-workshop`; rechazar otros clientes sin ampliar el contrato HTTP.
- [x] 2.3 Confirmar que este backend no tiene frontera de validacion entrante; el consumidor debe rechazar tokens expirados. No agregar endpoint ni filtro en este Issue.
- [x] 2.4 Exigir `AUTH_TOKEN_SECRET` por entorno sin valor por defecto; usar un valor ficticio en pruebas y no publicar secretos reales.

## 3. Especificar pruebas del contrato de token

- [x] 3.1 Agregar pruebas del componente de token para exigir `sub`, `client`, `iat` y `exp`, y comprobar que `client` corresponde al valor aceptado del request.
- [x] 3.2 Agregar pruebas deterministas de `exp` y `expiresIn` en segundos, derivados del mismo instante de emision y dentro de una tolerancia documentada de un segundo; controlar el tiempo sin esperas reales.
- [x] 3.3 Agregar pruebas para cliente no permitido y credenciales invalidas que verifiquen que no se emite un token autenticado, sin fijar nuevos status HTTP ni cuerpos de error.
- [x] 3.4 Verificar que el token emitido contiene un `exp` futuro coherente con `iat` y `expiresIn`; no probar rechazo de expiracion, que corresponde al consumidor.
- [x] 3.5 Probar rechazo de secreto ausente/corto y que los logs de error de lookup no expongan credenciales ni SQL.

## 4. Implementar la proteccion del secreto

- [x] 4.1 Configurar `AUTH_TOKEN_SECRET` como propiedad obligatoria sin valor por defecto; configurar en pruebas solo un valor ficticio.
- [x] 4.2 Eliminar la exposicion de datos de autenticacion en logs de lookup; conservar un mensaje de diagnostico generico.

## 5. Implementar emision e integracion incremental

- [x] 5.1 Sustituir la codificacion Base64 sin firma por JWT HS256 con Auth0 `java-jwt` e incluir `sub`, `client`, `iat` y `exp`.
- [x] 5.2 Validar `client` conforme a la politica de 2.2 y reflejar el valor validado en el claim `client`.
- [x] 5.3 Calcular `iat`, `exp` y `expiresIn` desde un mismo instante; mantener `expiresIn` en segundos y documentar la tolerancia acordada.
- [x] 5.4 Integrar la emision en `POST /auth/login`, conservando request, respuesta JSON directa, `token`, tratamiento de `user` y comportamiento de errores protegido; agregar `expiresIn` como campo superior.
- [x] 5.5 Mantener fuera de este cambio la validacion de tokens expirados; registrar que el consumidor debe aplicar y evidenciar ese rechazo antes de considerar completa la proteccion extremo a extremo.

## 6. Validar y cerrar evidencia

- [x] 6.1 Ejecutar las pruebas de autenticacion y del componente de token, incluyendo los casos de caracterizacion y los escenarios nuevos de ambas specs.
- [x] 6.2 Ejecutar `mvn verify` y comprobar la cobertura del codigo modificado conforme a las reglas del repositorio, sin silenciar gates de CI.
- [x] 6.3 Revisar la configuracion/documentacion de `AUTH_TOKEN_SECRET`, la decision Auth0 `java-jwt` HS256, la unidad/tolerancia de expiracion y la evidencia de logs redactados; confirmar que no contiene secretos reales.
- [x] 6.4 Ejecutar `openspec validate hu-01-token-claims-expiration --strict` y confirmar que el cambio OpenSpec sigue valido.