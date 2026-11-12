# Plan de aprendizaje

## Objetivo

Profundizar en Kafka, recuperación distribuida, rendimiento Java y observabilidad sobre un dominio financiero conocido. Cada incremento debe permitir explicar una decisión y comprobarla con evidencia. El código y las notas forman un portfolio público original.

## Itinerario propuesto

1. **Actual: publicación y proyección.** Un productor HTTP, tres particiones, un consumidor y una vista PostgreSQL. Distinguir confirmación del broker, procesamiento y commit de offset.
2. **Particiones y grupos.** Variar concurrencia, observar asignaciones y rebalances, estudiar orden por clave y concentración de tráfico. Añadir cambios de estado cuando acordemos el contrato.
3. **Fallos y replay.** Interrumpir después del commit SQL y antes del commit de offset; reconstruir una vista. Separar idempotencia técnica, de negocio y de HTTP.
4. **Contratos y consistencia.** Evolución de esquema y transactional outbox cuando exista una escritura de negocio que deba publicar un evento.
5. **Kafka Streams.** Ventanas temporales, eventos tardíos, agregaciones y alertas sobre operaciones sintéticas.
6. **Diagnóstico y rendimiento.** OpenTelemetry, latencia de extremo a extremo, lag, SLO, profiling y virtual threads; medir antes y después.
7. **Interfaz y operación.** Panel React/TypeScript, CI y despliegue cuando los ejercicios previos justifiquen esas piezas.

Las etapas posteriores son orientación, no autorización para implementarlas de una vez.

## Método por sesión

- Formular una pregunta o hipótesis.
- Discutir el comportamiento y los compromisos importantes.
- Implementar un cambio pequeño y revisarlo por flujo de ejecución.
- Acordar y autorizar el experimento o las pruebas.
- Comparar resultados con la hipótesis y anotar qué trasladar al trabajo.
- Dejar una modificación pequeña para que el usuario la haga o la razone antes de que Codex la resuelva.

## Estado

- Aprobados: enfoque del laboratorio y primer flujo productor → Kafka → proyección PostgreSQL.
- Implementado: primera versión del flujo y entorno local reproducible.
- Validación realizada: empaquetado Maven con Java 27 correcto (`BUILD SUCCESS`, tests omitidos), configuración Compose válida y sintaxis de scripts/XML comprobada.
- No validado todavía: arranque de la aplicación y recorrido HTTP → Kafka → PostgreSQL. No se han ejecutado tests ni experimentos.
- Pendiente: revisión conjunta del código y autorización de pruebas/experimentos.

## Decisiones de la primera versión

- Un único proceso Spring Boot evita distraernos con despliegues; productor y consumidor usan Kafka de verdad.
- SQL con JdbcClient hace visible el límite de la escritura sin añadir un ORM ni otra biblioteca nueva.
- `paymentId` es la clave; tres particiones permitirán experimentar. Con un solo evento por pago todavía no demostramos orden entre actualizaciones.
- Solo existe `PaymentSubmitted`: una vista inmutable que admite redelivery. No es un ledger ni un saldo.
- El productor no escribe PostgreSQL; por eso aún no hay un problema de doble escritura que justifique outbox.
- Una excepción detiene el listener. Retries, backoff y tratamiento de registros inválidos requieren una política que estudiaremos después.
- Broker único y factor de réplica 1: el laboratorio inicial no permite demostrar alta disponibilidad ni tolerancia a la pérdida del broker.
- Últimas versiones estables de componentes directos, versiones exactas y dependencias Spring alineadas por BOM.
