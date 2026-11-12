package io.github.jiog.fintech.payments;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;
    private final PaymentProjection projection;
    private final String topic;

    public PaymentController(KafkaTemplate<String, String> kafka, JsonMapper json,
                             PaymentProjection projection, @Value("${lab.payments-topic}") String topic) {
        this.kafka = kafka;
        this.json = json;
        this.projection = projection;
        this.topic = topic;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Publication>> submit(@Valid @RequestBody Submission request) {
        var event = new PaymentSubmitted(UUID.randomUUID(), request.amount(), request.currency(), Instant.now());
        // Broker acknowledgement precedes the HTTP response; projection happens independently.
        return kafka.send(topic, event.paymentId().toString(), json.writeValueAsString(event))
                .thenApply(result -> ResponseEntity.accepted()
                        .location(URI.create("/api/payments/" + event.paymentId()))
                        .body(new Publication(event.paymentId(), result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset())));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentProjection.View> find(@PathVariable UUID paymentId) {
        return ResponseEntity.of(projection.find(paymentId));
    }

    public record Submission(
            @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 15, fraction = 4)
            BigDecimal amount,
            @NotNull @Pattern(regexp = "EUR|USD|GBP") String currency) {
    }

    public record Publication(UUID paymentId, int partition, long offset) {
    }
}
