package io.github.jiog.fintech.payments;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSubmitted(UUID paymentId, BigDecimal amount, String currency, Instant occurredAt) {
}
