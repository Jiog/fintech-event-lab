# Lección 01 — Publicar no es procesar

## Pregunta

¿Qué sabemos exactamente cuando el cliente recibe un 202 de nuestra API?

## Sigue el recorrido

1. `PaymentController` valida el importe y construye un `PaymentSubmitted` con un UUID nuevo.
2. Serializa el evento a JSON y lo publica con `paymentId` como clave.
3. La respuesta HTTP espera a que termine el future de Kafka. Incluye partición y offset: el offset identifica una posición dentro de esa partición, no una secuencia global.
4. Independientemente, `PaymentConsumer` recibe el registro y llama a `PaymentProjection`.
5. PostgreSQL confirma el INSERT. Solo después vuelve el listener y Spring Kafka puede confirmar su offset (`ack-mode: record`, auto-commit desactivado).
6. El GET lee la proyección, que puede no existir todavía cuando llega el 202.

## Tres distinciones

**Kafka frente a RabbitMQ/JMS.** Partimos de tu experiencia con mensajería. Aquí observaremos un log particionado y posiciones por grupo: consumir no elimina el registro. El ejercicio posterior comparará retención, replay y asignación con tus patrones habituales.

**Idempotencia del productor frente a idempotencia de negocio.** `enable.idempotence` ayuda con reintentos del protocolo de Kafka. Dos POST crean dos UUID distintos y dos operaciones. No resolvemos aún el caso de respuesta HTTP perdida y reintento del cliente.

**Escritura SQL frente a commit de offset.** Si el proceso cae después del INSERT y antes de confirmar el offset, el mismo evento puede volver. La clave primaria y `ON CONFLICT DO NOTHING` evitan una segunda fila para ese pago inmutable. Esto no vuelve atómica la escritura entre Kafka y PostgreSQL, ni resuelve estados posteriores. Un evento externo con el mismo ID y contenido diferente también sería ignorado: el contrato de esta primera lección solo admite el evento original generado por nuestra API.

`acks: all` con un broker y factor de réplica 1 no demuestra redundancia. Tampoco implica que el consumidor ya haya procesado el mensaje.

## Antes de ejecutar

Revisa primero el controlador y la configuración. Intenta responder:

1. Si el listener está detenido y el broker sigue disponible, ¿puede el POST devolver 202?
2. Si el POST devuelve un error por timeout, ¿podemos asegurar que Kafka no recibió el evento?
3. ¿Qué información necesitaríamos para distinguir un pago inexistente de uno pendiente de proyección?

## Experimento pendiente de autorización

- Publicar una operación y relacionar el ID, la partición y el offset de la respuesta con la fila en PostgreSQL y los logs.
- Consultar el grupo y distinguir `CURRENT-OFFSET`, `LOG-END-OFFSET` y `LAG`.
- En la siguiente lección, detener el consumidor manteniendo un productor activo y observar la divergencia entre publicación y proyección.

No se ha ejecutado este experimento como parte de la creación de los archivos.

## Cómo pedir ayuda a Codex

> Explícame el recorrido del mensaje desde el POST hasta el commit del offset. Señala dónde podría repetirse trabajo si cae el proceso. No cambies código ni ejecutes comandos todavía.

> Revisa mi explicación de la garantía de entrega. Busca supuestos incorrectos y hazme una pregunta antes de darme la solución completa.
