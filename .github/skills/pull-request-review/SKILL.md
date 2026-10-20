---
name: pull-request-review
description: Revisa Pull Requests y diffs para encontrar errores funcionales, regresiones, riesgos de compatibilidad y problemas de pruebas. Úsalo al revisar un PR, un cambio propuesto o un diff antes de aprobarlo.
---

# Skill: Pull Request Review

## 1) Metadata
- **Skill ID:** pull-request-review
- **Purpose:** Identificar problemas concretos introducidos por un Pull Request y comunicar hallazgos accionables para que el autor pueda resolverlos.
- **Audience (roles):** Developers, Tech Lead, QA, Reviewer.
- **When to use:** Antes de aprobar o fusionar un PR, o cuando se solicite revisar un diff o cambio propuesto.
- **When NOT to use:** Para implementar cambios, hacer una auditoría de seguridad exhaustiva o revisar el sistema completo sin un cambio delimitado.
- **Sensitive inputs:** No incluyas credenciales, tokens, datos reales de clientes ni otra información sensible en el input o la salida.
- **Expected output:** Informe Markdown con hallazgos priorizados, evidencia y límites de la revisión.

## 2) Inputs
Proporciona lo que esté disponible:

- **Título o enlace del PR:**
- **Objetivo y contexto del cambio:**
- **Diff o archivos modificados:**
- **Rama base y rama propuesta o commits comparados:**
- **Criterios de aceptación o comportamiento esperado:**
- **Pruebas ejecutadas y resultados:**
- **Restricciones o áreas de especial atención:**

Un diff o un conjunto de archivos modificados es necesario para revisar el cambio. Si solo se proporciona un enlace inaccesible, solicita el diff o el contenido relevante; no afirmes haberlo inspeccionado.

## 3) Procedure
1. **Delimita el cambio.** Identifica qué archivos y líneas fueron modificados. Si no se puede determinar la diferencia con la rama base, declara esa limitación.
2. **Comprende el comportamiento esperado.** Usa el contexto del PR, los criterios de aceptación y el código existente; no inventes requisitos.
3. **Inspecciona el contexto necesario.** Lee las funciones relacionadas, sus usos y las pruebas cercanas para entender el impacto, sin ampliar la revisión a código no relacionado.
4. **Busca problemas introducidos por el cambio.** Prioriza errores funcionales, regresiones, manejo incorrecto de errores o datos, compatibilidad y cobertura insuficiente de escenarios importantes. Considera riesgos de seguridad evidentes, pero no presentes una revisión general como una auditoría de seguridad.
5. **Valida cada posible hallazgo.** Comprueba que sea atribuible al cambio, que tenga un escenario concreto y que pueda corregirse. Descarta preferencias de estilo, especulaciones y problemas preexistentes no agravados por el PR.
6. **Evalúa las pruebas con precisión.** Informa qué pruebas aparecen en la evidencia recibida o cuáles se ejecutaron realmente. Nunca afirmes que una prueba pasó si no hay resultado verificable.
7. **Entrega el informe** con el formato de Output. Ordena los hallazgos por severidad y luego por confianza. No edites archivos ni publiques comentarios en el PR.

## 4) Output
Devuelve un documento Markdown en el idioma del usuario con estas secciones:

1. **Resumen:** resultado breve de la revisión.
2. **Hallazgos:** lista priorizada. Para cada uno incluye:
   - Severidad (`P0` bloqueante, `P1` alta, `P2` media o `P3` baja).
   - Archivo y línea modificada o contexto exacto.
   - Problema y efecto observable.
   - Escenario que lo activa.
   - Corrección sugerida.
   - Confianza de 1 a 10.
3. **Validación:** pruebas y evidencia revisadas; separa claramente las ejecutadas de las no ejecutadas.
4. **Alcance y limitaciones:** indica si se revisó un diff parcial, si faltó contexto o si hay preguntas pendientes.

Si no se encuentran problemas, escribe explícitamente **“No se encontraron hallazgos en el alcance revisado”**. Esto no equivale a garantizar que el cambio esté libre de defectos.

## 5) Constraints
- Reporta solo problemas concretos introducidos o agravados por el PR.
- Cada hallazgo debe apuntar a una línea modificada o al fragmento más cercano que permita localizarlo.
- Explica el impacto para el usuario o el sistema; evita frases vagas como “esto podría fallar”.
- No conviertas recomendaciones de estilo, preferencias personales o falta de contexto en hallazgos.
- No inventes resultados de pruebas ni afirmes que se ejecutaron comandos que no se ejecutaron.
- No modifiques código, no apruebes ni rechaces el PR en nombre del usuario, y no publiques comentarios sin autorización explícita.
- Si no hay suficiente información para validar un posible problema, indícalo como limitación o pregunta abierta; no lo presentes como hallazgo confirmado.

## 6) Validation Rules
La salida es válida si:
- Distingue hallazgos confirmados de limitaciones y preguntas.
- Cada hallazgo tiene severidad, ubicación, impacto, escenario y confianza.
- Los hallazgos están ordenados por severidad y confianza.
- La validación describe con veracidad qué evidencia de pruebas se revisó o ejecutó.
- No contiene problemas preexistentes como si fueran introducidos por el PR.
- Si no hay hallazgos, comunica el alcance de la revisión y evita garantizar ausencia de defectos.

## 7) Examples
- [Input de ejemplo](./examples/input.sample.md)
- [Output de ejemplo](./examples/output.sample.md)
