## Summary
- Ajustar `POST /auth/login` para emitir tokens firmados con los claims mínimos `sub`, `client`, `iat` y `exp`, y asegurar la coherencia entre `expiresIn` y la expiración.
- Evitar registrar tokens, secretos y valores sensibles, y documentar la configuración del secreto.

## Scope (in/out)
- **In scope**
  - Obtener `client` de la solicitud, validarlo e incluirlo en el token.
  - Comprobar la relación entre `expiresIn` y `exp - now` dentro de una tolerancia documentada.
  - Resolver y documentar el manejo del secreto por defecto fuera del laboratorio.
  - Mantener como decisión técnica el uso de HMAC o de una librería JWT.
- **Out of scope**
  - No definido en la HU.

## Assumptions
- La HU no declara supuestos adicionales.

## Technical decisions to make
- Decidir si se conserva HMAC o se adopta una librería JWT y documentar la decisión.
- Definir la tolerancia para comprobar `expiresIn` frente a `exp - now`.
- Determinar si se elimina el secreto por defecto fuera del laboratorio o se documenta como exclusivo del laboratorio.

## Risks and mitigations
- **Uso de un secreto por defecto fuera del laboratorio:** eliminarlo o documentarlo como exclusivo del laboratorio, según la decisión acordada.
- **Exposición de tokens o secretos en logs:** comprobar que el registro no incluya tokens, secretos ni valores sensibles.
- **Claims o expiración incorrectos:** probar los claims requeridos y la coherencia de `expiresIn` con `exp - now` dentro de la tolerancia documentada.

## Acceptance criteria
- Un token válido contiene los claims `sub`, `client`, `iat` y `exp`.
- El claim `client` corresponde al valor obtenido de la solicitud y validado.
- `expiresIn` coincide con `exp - now` dentro de la tolerancia documentada.
- Hay pruebas para login válido, credenciales inválidas y token expirado.
- No se registran tokens, secretos ni valores sensibles.
- La decisión entre HMAC y una librería JWT queda documentada.
- La configuración de `AUTH_TOKEN_SECRET` queda documentada.

## Validation plan
- **Unit tests**
  - Comprobar presencia de los cuatro claims requeridos y el valor validado de `client`.
  - Comprobar la coherencia de `expiresIn` con `exp - now` dentro de la tolerancia acordada.
  - Cubrir las condiciones de login y expiración indicadas en la HU.
- **Functional tests**
  - Probar login válido, credenciales inválidas y uso de un token expirado.
- **Security checks**
  - Revisar que los logs no contengan tokens, secretos ni valores sensibles.
  - Revisar la configuración documentada de `AUTH_TOKEN_SECRET` respecto a su uso fuera del laboratorio.
- **Evidence required**
  - Resultados de las pruebas de claims, expiración y errores de autenticación.
  - Documento con la decisión HMAC/JWT y la configuración del secreto.
  - Evidencia de revisión de logs redactados.

## Suggested tasks by role
- **Chapter/PO**
  - Confirmar el alcance de la HU y las decisiones pendientes que requieren definición funcional.
  - Revisar que los criterios de aceptación reflejen la historia.
- **Tech Lead**
  - Decidir y documentar HMAC frente a una librería JWT.
  - Definir la tolerancia para la coherencia de expiración.
  - Acordar el tratamiento del secreto por defecto fuera del laboratorio.
- **Backend**
  - Implementar los claims requeridos y obtener y validar `client` desde la solicitud.
  - Implementar la comprobación de `expiresIn` conforme a la tolerancia acordada.
  - Evitar el registro de tokens, secretos y valores sensibles; documentar la configuración del secreto.
- **Frontend**
  - La HU no especifica cambios de frontend; confirmar si se requiere alguno.
- **QA**
  - Probar login válido, credenciales inválidas y token expirado.
  - Verificar claims y coherencia de expiración según la tolerancia documentada.
  - Adjuntar los resultados de las pruebas como evidencia.
- **Cyber**
  - Revisar que los logs no expongan tokens, secretos ni valores sensibles.
  - Revisar la configuración documentada del secreto por defecto.

## Open questions
- ¿Cuál es la tolerancia documentada para comparar `expiresIn` con `exp - now`?
- ¿Se eliminará el secreto por defecto fuera del laboratorio o se documentará como exclusivo del laboratorio?
- ¿Se conservará HMAC o se adoptará una librería JWT?
- ¿Qué valor debe representar `sub` y cómo se obtiene?
- ¿Qué reglas se usarán para validar `client`?
