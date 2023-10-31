package ru.alfastrah.api.tracing.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Custom tracing properties
 *
 * @author nosovni on 29.10.2023
 */
@ConfigurationProperties("custom.tracing")
public record CustomTracingProperties(
        /* Kafka Producer username */
        String username,
        /* Kafka Producer password */
        String password,
        /* Kafka Producer topic */
        String topic,
        /* Kafka Producer bootstrap servers */
        String bootstrapServers
) {
}
