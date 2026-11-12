package io.github.jiog.fintech.payments;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PaymentConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    private final JsonMapper json;
    private final PaymentProjection projection;

    public PaymentConsumer(JsonMapper json, PaymentProjection projection) {
        this.json = json;
        this.projection = projection;
    }

    @KafkaListener(topics = "${lab.payments-topic}")
    public void consume(ConsumerRecord<String, String> record) {
        var event = json.readValue(record.value(), PaymentSubmitted.class);
        if (event.paymentId() == null || !event.paymentId().toString().equals(record.key())) {
            throw new IllegalArgumentException("Kafka key must match paymentId");
        }
        projection.apply(event, record.partition(), record.offset());
        log.info("Projected payment={} partition={} offset={}", event.paymentId(), record.partition(), record.offset());
    }
}
