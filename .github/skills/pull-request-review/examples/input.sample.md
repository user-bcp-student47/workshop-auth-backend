# Ejemplo de input: revisión de Pull Request

- **Título o enlace del PR:** Limitar a diez los productos del resumen
- **Objetivo y contexto del cambio:** El endpoint de resumen debe devolver como máximo diez productos, conservando todos cuando hay menos de diez.
- **Diff o archivos modificados:** `src/main/java/com/example/catalog/CatalogService.java`
- **Rama base y rama propuesta o commits comparados:** `main` contra `feature/summary-limit`
- **Criterios de aceptación o comportamiento esperado:**
  - Con más de diez productos, devolver los primeros diez.
  - Con diez o menos productos, devolver todos sin error.
- **Pruebas ejecutadas y resultados:** No se adjuntaron resultados de pruebas.
- **Restricciones o áreas de especial atención:** Revisar el comportamiento con listas vacías y con menos de diez elementos.

```diff
diff --git a/src/main/java/com/example/catalog/CatalogService.java b/src/main/java/com/example/catalog/CatalogService.java
--- a/src/main/java/com/example/catalog/CatalogService.java
+++ b/src/main/java/com/example/catalog/CatalogService.java
@@ -21,7 +21,7 @@ public List<Product> summary(List<Product> products) {
-    return products.stream().limit(10).toList();
+    return products.subList(0, 10);
 }
```
