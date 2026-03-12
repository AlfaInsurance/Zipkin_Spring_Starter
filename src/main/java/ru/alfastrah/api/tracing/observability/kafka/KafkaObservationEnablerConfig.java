package ru.alfastrah.api.tracing.observability.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class KafkaObservationEnablerConfig {

    @Bean
    public KafkaObservationEnablerPostProcessor kafkaObservationEnablerPostProcessor() {
        return new KafkaObservationEnablerPostProcessor();
    }
}