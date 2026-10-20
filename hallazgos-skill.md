# Hallazgos candidatos - Auth Backend

Revision de solo lectura. Este documento contiene un candidato; requiere revision humana y no crea issues.

### CORS acepta cualquier origen
- Estado: CANDIDATO
- Evidencia: `src/main/resources/application.properties:4-8` habilita CORS y configura `quarkus.http.cors.origins=/.*/`; tambien permite los metodos GET, POST, PUT, DELETE y OPTIONS y el header Authorization.
- Impacto: amplia a cualquier origen web la posibilidad de iniciar solicitudes cross-origin al servicio. Esto no concede por si solo autenticacion ni acceso a datos, pero puede exponer operaciones que el navegador ya pueda ejecutar y leer.
- Prioridad propuesta: P2, condicionada a confirmar el modelo de autenticacion y los origenes que el laboratorio debe admitir.
- Contrato potencialmente afectado: politica CORS del backend y consumo browser-based de `POST /auth/login` y `/q/health`.
- Como validarlo: realizar preflight desde un origen de prueba no confiable y comprobar origen/metodos/headers permitidos; contrastar el resultado con la lista de origenes esperada para el laboratorio.
