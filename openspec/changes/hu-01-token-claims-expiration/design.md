# Design: HU-01 Token firmado con expiracion y claims minimos

## Contexto tecnico

- `AuthResource.login` implementa `POST /auth/login`: valida campos requeridos, consulta `LegacyDatabase`, crea el token y arma directamente la respuesta. La validacion, acceso a datos y serializacion estan acoplados en el mismo recurso.
- `LoginRequest` contiene `username`, `password` y `client`. El campo `client` ya llega desde el frontend (`angular-workshop` segun `docs/ENDPOINTS.md`), pero el backend actual no lo valida ni lo incluye en el token.
- `LegacyTokenUtil` no implementa HMAC ni JWT: concatena username, role, expiracion y el secreto literal `WORKSHOP_SECRET_2026`, y codifica el resultado con Base64. No hay verificador de tokens en este backend ni dependencia JWT en `pom.xml`.
- La expiracion se calcula como `now + 3600` dentro de `LegacyTokenUtil`, mientras `AuthResource` vuelve a escribir `expiresIn: 3600` por separado. Ambas cantidades pueden divergir si se cambian independientemente.
- `application.properties` no declara `AUTH_TOKEN_SECRET`. `LegacyDatabase` imprime excepciones con `printStackTrace`; como la consulta construye SQL concatenando las credenciales, una excepcion podria revelar valores del request.
- La prueba actual de `AuthResource` solo caracteriza el login valido, HTTP 200, username de respuesta y presencia del token. No cubre cliente rechazado, expiracion, credenciales invalidas ni logs.
- El Issue #10 actualizado confirma JWT mediante una libreria mantenida, `angular-workshop` como unico cliente aceptado y `AUTH_TOKEN_SECRET` desde el entorno sin fallback. No hay frontera de validacion de tokens en este backend; solo se emite `exp` y la validacion corresponde al consumidor.

## Goals

- Cumplir las dos capabilities de las specs: claims y expiracion coherentes en la emision, y proteccion del secreto/logs.
- Conservar la superficie de `POST /auth/login` y la respuesta JSON directa consumida por el frontend.
- Hacer observables mediante pruebas los casos de login exitoso, credenciales invalidas, cliente rechazado y claims emitidos.
- Usar Auth0 `java-jwt` con HS256 para sustituir la codificacion Base64 heredada, que no proporciona integridad ni firma.
- Exigir `AUTH_TOKEN_SECRET` desde configuracion del entorno en todos los perfiles, sin valor por defecto; las pruebas usaran una clave ficticia de al menos 32 bytes UTF-8.
- Limitar el trabajo a autenticacion y a la configuracion/logging que participa en ese flujo.

## Non-goals

- Cambiar rutas, request fields, el esquema de `user`, la API de tareas, persistencia, contrasenas o CORS.
- Introducir refresh, revocacion, sesiones persistentes o un nuevo endpoint de validacion.
- Redefinir codigos HTTP o cuerpos de error que no forman parte del contrato de `docs/ENDPOINTS.md`.
- Agregar una frontera de validacion de JWT, endpoint o filtro; no existe una frontera de autenticacion entrante en este repositorio.
- Corregir deuda general de `LegacyDatabase` o refactorizar toda la aplicacion.

## Contratos protegidos

| Superficie | Regla para este cambio |
|---|---|
| Request | Mantener `POST /auth/login` y el body JSON con `username`, `password` y `client`. El cliente documentado `angular-workshop` debe seguir siendo aceptado. |
| Exito | Mantener la respuesta JSON directa, sin wrapper, con `token` y el tratamiento existente de `user`; agregar `expiresIn` como campo superior. No depender de un status de exito mas especifico que el contrato documentado. |
| Error | No cambiar deliberadamente el comportamiento HTTP/cuerpo existente como parte del trabajo de token. Las pruebas nuevas no deben inventar un esquema de error; el caso actual de credenciales invalidas se caracteriza para detectar regresiones. |
| Token | Emitir un JWT firmado con HS256 usando Auth0 `java-jwt`, con `sub`, `client`, `iat` y `exp`. Aceptar unicamente `angular-workshop`. La codificacion Base64 actual no se considera una firma ni una garantia de integridad. |
| Expiracion | Representar `expiresIn` en segundos, alineado con `exp` en epoch seconds y el TTL de 3600 segundos. Derivar `iat`, `exp` y `expiresIn` de un mismo instante de emision; documentar una tolerancia maxima de un segundo por granularidad de epoch seconds. El consumidor valida y rechaza tokens expirados; este backend no valida tokens entrantes. |
| Datos sensibles | No registrar token, secreto, password ni el request completo. Los logs de errores de consulta dentro del flujo de login deben evitar volcar SQL o excepciones que puedan contener credenciales. |

`debug` aparece hoy en el JSON de exito, pero no forma parte del contrato baseline del frontend ni de los criterios del Issue. Este cambio no debe convertirlo en un requisito nuevo ni ampliar el payload con diagnosticos; cualquier decision de eliminarlo debe tratarse aparte para no mezclar alcance.

## Componentes afectados

- `AuthResource`: orquestacion del login y construccion compatible de la respuesta. Su responsabilidad actual combina validacion, consulta y respuesta; cualquier separacion debe limitarse a la ruta necesaria para el token.
- `LoginRequest`: fuente del `client`; la politica de valores aceptados debe quedar definida, manteniendo `angular-workshop`.
- `TokenService`: emision de JWT HS256, claims y duracion; la clave viene de `auth.token.secret`/`AUTH_TOKEN_SECRET`.
- Configuracion Quarkus (`application.properties` y perfiles/variables de entorno): origen y politica del secreto `AUTH_TOKEN_SECRET`, sin valor real por defecto fuera del sandbox.
- `LegacyDatabase`: solo el limite de logging alcanzado por el flujo de login; no incluye redisenar consultas ni persistencia.
- `AuthResourceTest` y pruebas unitarias nuevas del componente de token/configuracion: caracterizacion HTTP, claims, expiracion, cliente, errores y ausencia de datos sensibles en logs.
- Documentacion/evidencia de configuracion: registrar la decision HMAC/JWT, la unidad/tolerancia de expiracion y el uso permitido del secreto.

## Decisiones tecnicas y alternativas

### Representacion y firma del token

**Decision aprobada:** usar Auth0 `java-jwt` con HS256. El estado actual es Base64 sin firma, por lo que sustituirlo por JWT firmado corrige la ausencia de integridad y adopta un formato estandar de claims/expiracion con una libreria mantenida. La dependencia es Java y se integra sin requerir una frontera de validacion Quarkus en este backend.

La clave se obtiene de `AUTH_TOKEN_SECRET`, nunca de un literal o valor por defecto. HS256 requiere una clave de al menos 32 bytes UTF-8; las pruebas usan solo una clave ficticia. Los validadores de consumidores no inspeccionados no se presumen compatibles con el nuevo formato y deben confirmarse antes del despliegue.

### Tiempo y expiracion

**Decision:** `expiresIn` se expresa en segundos y se calcula con el mismo instante de emision usado para `iat` y `exp`; la tolerancia documentada y comprobada sera de un segundo. Para pruebas, el instante debe poder fijarse o controlarse sin dormir ni depender del reloj de pared.

### Validacion de `client`

**Decision aprobada:** permitir exclusivamente `angular-workshop`. Cualquier otro valor se rechaza sin fijar un nuevo status HTTP o cuerpo de error.

### Token expirado

Este backend hoy emite tokens pero no tiene una ruta ni componente que los valide. El cambio solo prueba que el token emitido contiene un `exp` futuro coherente con `expiresIn`; no prueba rechazo de tokens expirados. Ese enforcement pertenece al consumidor que verifica el JWT y requiere su propio alcance aprobado. No se agrega un endpoint ni filtro a este backend.

## Estrategia incremental

1. **Caracterizar antes del cambio:** ampliar primero las pruebas del recurso para fijar request JSON existente, respuesta directa con `token` y `user`, login valido, credenciales invalidas y campos requeridos. Registrar el comportamiento actual de `expiresIn` y del campo `debug` como observaciones, no como nuevos requisitos si no estan en el contrato.
2. **Decisiones aprobadas:** Auth0 `java-jwt` con HS256, unico cliente `angular-workshop`, secreto obligatorio por entorno sin fallback, y validacion de expiracion delegada al consumidor.
3. **Probar el contrato del token en aislamiento:** verificar claims, correspondencia de `client`, calculo de `iat`/`exp` y `expiresIn`, tolerancia y expiracion emitida con tiempo controlado. Cubrir cliente no aceptado sin fijar un nuevo status/cuerpo HTTP.
4. **Integrar el login:** conectar la emision al flujo existente y comprobar por prueba de recurso que el contrato documentado sigue intacto y que `expiresIn` es aditivo.
5. **Verificar secretos y logs:** probar configuracion por entorno y revisar logs de login exitoso, fallido y error de consulta con valores sentinel; no volcar excepciones/SQL que puedan contener datos sensibles.
6. **Cerrar evidencia:** documentar configuracion, unidad/tolerancia, decision criptografica y resultados de pruebas. Mantener el cambio acotado a estas capabilities.

## Pruebas de caracterizacion previas

- Login valido con el body documentado, incluyendo `client: angular-workshop`; verificar respuesta JSON directa, `token` y los datos de `user` que consume el frontend.
- Credenciales incorrectas: fijar el resultado real actual (401 y sin token), evitando ampliar el contrato con un nuevo formato de error.
- Usuario/contrasena ausentes o vacios: fijar las ramas actuales para evitar cambios accidentales en validacion HTTP.
- Verificar que el campo `client` actualmente se ignora y que el token actual codifica username/role/expiry; esta observacion permite hacer visible el cambio intencional, no debe preservarse como requisito de seguridad.
- Capturar la respuesta actual respecto a `expiresIn` (3600 fijo) y `debug`, distinguiendo implementacion observada de contrato protegido.
- Observar stdout/stderr en errores de autenticacion/consulta usando credenciales sentinel para detectar exposicion en stack traces.

Las pruebas de caracterizacion no deben afirmar que Base64 es una firma valida ni codificar el secreto literal en nuevos fixtures. Para expiracion, probar el validador elegido con reloj controlado; no usar esperas reales.

## Riesgos y trade-offs

- **Cambio de formato:** el Issue actualizado aprueba JWT, mientras que el codigo actual concatena datos y los codifica en Base64. No se debe presentar ese formato heredado como firma.
- **Compatibilidad de consumidores:** reemplazar el formato actual puede romper consumidores que dependan de parsear el token Base64. `docs/ENDPOINTS.md` solo confirma que el frontend reenvia el token; no verifica todos los validadores aguas abajo. Validar compatibilidad antes de desplegar y no inspeccionar otros repositorios como parte de este alcance.
- **Cliente permitido sin definir:** rechazar `client` exige politica; aceptar cualquier string no satisface validacion significativa. `angular-workshop` es el unico valor documentado localmente.
- **Expiracion sin validador local:** este backend solo emite `exp`; el rechazo del token expirado no tendra evidencia end-to-end hasta que el consumidor implemente y pruebe su validacion en un alcance separado.
- **Secretos y errores de persistencia:** el literal actual es publico en codigo y `printStackTrace` puede exponer detalles de la consulta. Limitar la mitigacion a logs del flujo de autenticacion evita convertir el cambio en un refactor de base de datos.
- **Respuesta compatible vs. datos extra:** `expiresIn` es aditivo para el frontend; `debug` no esta documentado como contrato, pero retirarlo ahora seria un cambio ajeno al Issue.
- **JWT y compatibilidad:** la nueva dependencia reduce codigo criptografico propio pero agrega mantenimiento, y consumidores que parseen el token heredado pueden requerir cambios antes del despliegue.

## Rollback

- No se prevén cambios de esquema de base de datos ni migraciones persistentes; el rollback principal es revertir el artefacto de aplicacion y la configuracion de `AUTH_TOKEN_SECRET` como una unidad.
- Si cambia el formato del token, el binario anterior puede no entender tokens ya emitidos. El rollback puede requerir que los usuarios vuelvan a iniciar sesion; el TTL actual observado es de una hora, pero no debe asumirse como contrato sin la decision final.
- No retirar ni rotar la clave usada por el binario que se restaura hasta confirmar su compatibilidad. Evitar despliegues parcialmente revertidos con codigo y configuracion de claves de generaciones distintas.
- La estrategia final de compatibilidad/roll-forward depende de la decision HMAC/JWT y de los consumidores confirmados; no introducir emision dual sin aprobar ese alcance.