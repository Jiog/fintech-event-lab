package io.github.jiog.fintech.payments;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.CommonErrorHandler;

@Configuration
public class KafkaConfiguration {
    @Bean
    NewTopic paymentsTopic(@Value("${lab.payments-topic}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    CommonErrorHandler stopOnConsumerFailure() {
        // Make failures visible in lesson 01 instead of recovering by silently skipping a record.
        return new CommonContainerStoppingErrorHandler();
    }
}
