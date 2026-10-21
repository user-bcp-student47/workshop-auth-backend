## ADDED Requirements

### Requirement: Claims minimos para el token de login

El servicio SHALL incluir los claims `sub`, `client`, `iat` y `exp` en cada token emitido tras un login exitoso.

#### Scenario: Login valido emite los claims requeridos

- GIVEN una solicitud de login con credenciales validas y un cliente aceptado
- WHEN el servicio emite el token
- THEN el token contiene `sub`, `client`, `iat` y `exp`

### Requirement: El cliente del request se valida y se refleja

El servicio SHALL validar el campo `client` del body JSON antes de emitir un token, y el claim `client` SHALL corresponder al valor validado de esa solicitud.

#### Scenario: Cliente aceptado aparece en el token

- GIVEN una solicitud con credenciales validas y un valor `client` aceptado
- WHEN el servicio emite el token
- THEN el claim `client` refleja ese valor validado

#### Scenario: Cliente no aceptado no autentica

- GIVEN una solicitud con credenciales validas y un valor `client` no aceptado
- WHEN se procesa el login
- THEN no se emite un token que autentique esa solicitud
- AND no se requiere un status HTTP ni un cuerpo de error concreto por este requisito

### Requirement: La respuesta de login conserva el contrato y declara la expiracion

El servicio SHALL conservar `POST /auth/login`, el body JSON existente (`username`, `password`, `client`) y la respuesta JSON directa que el frontend consume. En una respuesta exitosa, SHALL conservar `token` y el tratamiento existente de `user`, y SHALL añadir `expiresIn` como campo de nivel superior. El campo `expiresIn` SHALL representar `exp - now` dentro de una tolerancia documentada; la unidad y la tolerancia SHALL quedar documentadas.

#### Scenario: Login exitoso conserva campos y añade expiresIn

- GIVEN el frontend envia `username`, `password` y `client` como propiedades del body JSON
- WHEN el login tiene exito
- THEN la respuesta mantiene el objeto JSON directo y el campo `token` requerido por el frontend
- AND conserva el tratamiento previo de `user` sin imponer un esquema nuevo
- AND incluye `expiresIn` como campo superior
- AND `expiresIn` coincide con `exp - now` dentro de la tolerancia y unidad documentadas

### Requirement: Las credenciales invalidas no producen un login exitoso

El servicio SHALL rechazar la autenticacion con credenciales invalidas y el cambio SHALL incluir una prueba observable de ese resultado. El status HTTP y el cuerpo de error concretos no se fijan aquí porque el contrato baseline no los especifica.

#### Scenario: Credenciales invalidas

- GIVEN una solicitud con credenciales invalidas
- WHEN se procesa el login
- THEN el login no se considera exitoso ni se entrega un token autenticado
- AND la prueba no presupone un status HTTP ni una forma de error no documentados

### Requirement: Los tokens expirados no autentican

El servicio SHALL rechazar un token una vez superado su claim `exp`, y SHALL incluir una prueba de token expirado. La representacion HTTP del rechazo SHALL seguir el contrato existente y no se concreta aquí.

#### Scenario: Token expirado

- GIVEN un token cuyo instante `exp` ya ha pasado
- WHEN se valida para autenticar una solicitud
- THEN la autenticacion se rechaza
- AND la prueba no presupone un status HTTP ni un cuerpo de error no documentados