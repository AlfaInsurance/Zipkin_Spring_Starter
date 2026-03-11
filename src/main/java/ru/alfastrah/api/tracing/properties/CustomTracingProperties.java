package ru.alfastrah.api.tracing.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Custom tracing properties
 *
 * @param username         Kafka Producer username
 * @param password         Kafka Producer password
 * @param topic            Kafka Producer topic
 * @param bootstrapServers Kafka Producer bootstrap servers
 * @param securityProtocol Kafka Producer security protocol
 * @param saslMechanism    Kafka Producer sasl mechanism
 */
@ConfigurationProperties("custom.tracing")
public record CustomTracingProperties(

        String username,
        String password,
        String topic,
        String bootstrapServers,
        String securityProtocol,
        String saslMechanism
) {
}
