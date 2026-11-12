package io.github.jiog.fintech.payments;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentProjection {
    private final JdbcClient jdbc;

    public PaymentProjection(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void apply(PaymentSubmitted event, int partition, long offset) {
        // This immutable submitted-payment view tolerates redelivery of the same event.
        // There is one event per payment in lesson 01; status updates need a different model.
        jdbc.sql("""
                INSERT INTO payment_projection
                    (payment_id, amount, currency, occurred_at, kafka_partition, kafka_offset)
                VALUES (:id, :amount, :currency, :occurredAt, :partition, :offset)
                ON CONFLICT (payment_id) DO NOTHING
                """)
                .param("id", event.paymentId())
                .param("amount", event.amount())
                .param("currency", event.currency())
                .param("occurredAt", event.occurredAt().atOffset(ZoneOffset.UTC))
                .param("partition", partition)
                .param("offset", offset)
                .update();
    }

    public Optional<View> find(UUID paymentId) {
        return jdbc.sql("""
                SELECT payment_id, amount, currency, occurred_at, projected_at, kafka_partition, kafka_offset
                FROM payment_projection WHERE payment_id = :id
                """)
                .param("id", paymentId)
                .query((rs, rowNum) -> new View(
                        rs.getObject("payment_id", UUID.class), rs.getBigDecimal("amount"),
                        rs.getString("currency"), rs.getObject("occurred_at", OffsetDateTime.class).toInstant(),
                        rs.getObject("projected_at", OffsetDateTime.class).toInstant(),
                        rs.getInt("kafka_partition"), rs.getLong("kafka_offset")))
                .optional();
    }

    public record View(UUID paymentId, BigDecimal amount, String currency, Instant occurredAt,
                       Instant projectedAt, int partition, long offset) {
    }
}
