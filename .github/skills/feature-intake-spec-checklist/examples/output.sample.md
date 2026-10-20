## Summary
- Add a pre-send confirmation, summary message, and data validation to the existing transfer flow.
- Expected impact: reduce complaints caused by duplicate transfers by 20% in one month.

## Scope
- **Dentro del alcance**
	- Presentar una confirmación antes de enviar una transferencia.
	- Mostrar un resumen y validar los datos de la transferencia.
	- Trabajar con la REST API, el frontend web y los servicios existentes de cuentas y transferencias.
- **Fuera del alcance**
	- Cambios de infraestructura.
	- Rediseño completo de la interfaz.

## Supuestos
- La implementación se limita al flujo existente y a las restricciones técnicas indicadas; los detalles no especificados quedan pendientes de decisión.

## Decisiones técnicas por tomar (pendientes o resueltas)
- **Resueltas:** usar la REST API y el frontend web existentes, mantener el registro estandarizado e integrar con los servicios de cuentas y transferencias.
- **Pendientes:** definir los campos del resumen, las reglas de validación, los datos considerados sensibles en logs y la línea base para medir las quejas por duplicados.

## Riesgos y mitigaciones
- **Exposición de datos sensibles en logs:** identificar qué datos son sensibles y comprobar que no se registren durante el flujo.
- **Errores en la validación del importe:** acordar las reglas de validación y verificar entradas válidas e inválidas antes del envío.
- **Dependencia de los servicios de cuentas y transferencias:** validar el flujo con ambos servicios y documentar los resultados y errores observados.

## Criterios de aceptación (medibles)
- El 100% de las transferencias válidas llega a la confirmación antes de enviarse al servicio de transferencias.
- El 100% de las entradas que incumplen las reglas de validación acordadas se bloquea antes del envío.
- El resumen se muestra antes del envío en el 100% de los flujos que llegan a confirmación.
- Los logs del flujo contienen 0 valores clasificados como sensibles.
- En el primer mes, las quejas por transferencias duplicadas disminuyen al menos un 20% frente a la línea base acordada.

## Plan de validación
- **Pruebas unitarias**
	- Verificar presentación de confirmación y resumen para transferencias válidas.
	- Verificar que las entradas inválidas según las reglas acordadas se bloquean.
	- Verificar que el registro del flujo no incluye valores sensibles.
- **Pruebas funcionales**
	- Recorrer el flujo de confirmación con los servicios de cuentas y transferencias.
	- Comprobar el comportamiento con datos válidos e inválidos.
	- Medir las quejas por duplicados durante un mes frente a la línea base acordada.
- **Seguridad (comprobaciones mínimas)**
	- Revisar los logs del flujo para detectar exposición de datos sensibles.
	- Comprobar que la validación ocurre antes de enviar la transferencia.
- **Evidencia requerida (qué adjuntar)**
	- Salida de las pruebas unitarias y funcionales.
	- Checklist de revisión de logs y validaciones.
	- Registro de la línea base y del resultado de medición al mes.

## Tareas sugeridas por rol
- **Chapter/PO**
	- Acordar la línea base y el método de conteo de quejas por transferencias duplicadas.
	- Definir los campos que debe mostrar el resumen.
	- Confirmar el periodo de medición de un mes y el objetivo de reducción del 20%.
- **Tech Lead**
	- Definir el contrato del flujo entre el frontend, la REST API y los servicios existentes.
	- Acordar las reglas de validación y los datos sensibles que no deben aparecer en logs.
	- Revisar el diseño de integración y los criterios de aceptación antes de implementar.
- **Backend**
	- Implementar la validación de datos según las reglas acordadas.
	- Integrar el flujo con los servicios existentes de cuentas y transferencias.
	- Añadir pruebas unitarias y verificar que los logs no exponen datos sensibles.
- **Frontend**
	- Implementar la confirmación previa al envío.
	- Mostrar el resumen con los campos acordados.
	- Verificar la presentación del flujo y los mensajes de validación con pruebas funcionales.
- **QA**
	- Preparar casos para entradas válidas e inválidas según las reglas acordadas.
	- Ejecutar pruebas funcionales de confirmación, resumen e integración.
	- Adjuntar resultados de pruebas y evidencias de aceptación.
- **Cyber**
	- Revisar el tratamiento de datos sensibles en los logs.
	- Comprobar que las validaciones se aplican antes del envío.
	- Documentar los resultados de las comprobaciones mínimas de seguridad.

## Preguntas abiertas
- ¿Qué campos debe incluir el resumen de la transferencia?
- ¿Cuáles son las reglas exactas de validación, incluido el importe?
- ¿Qué datos se consideran sensibles y deben excluirse de los logs?
- ¿Cuál es la línea base y cómo se contarán las quejas por transferencias duplicadas?