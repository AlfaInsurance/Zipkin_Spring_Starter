package ru.alfastrah.api.tracing.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.alfastrah.api.tracing.properties.CustomTracingProperties;
import ru.alfastrah.api.tracing.util.Constants;
import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka.KafkaSender;

import java.util.Map;

/**
 * Configures KafkaSender for sending traces to Zipkin via Kafka
 *
 * @author nosovni on 29.10.2023
 * @see KafkaProperties
 * @see CustomTracingProperties
 * @see KafkaSender
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({KafkaProperties.class, CustomTracingProperties.class})
@RequiredArgsConstructor
public class KafkaSenderConfiguration {

    private final CustomTracingProperties customTracingProperties;

    @Bean(Constants.ZIPKIN_SENDER_BEAN_NAME)
    public Sender kafkaSender(KafkaProperties config, Environment environment) {

        // Adding properties of Kafka for tracing
        final Map<String, Object> properties = config.buildProducerProperties();

        // Bootstrap-servers value fetched from CustomTracingProperties and then splitted to List
        properties.put(Constants.BOOTSTRAP_SERVERS_PROP_KEY,
                Constants.Helpers.STRING_TO_LIST.apply(customTracingProperties.bootstrapServers(), ","));

        // Key/Value serializers
        properties.put(Constants.KEY_SERIALIZER_PROP_KEY, ByteArraySerializer.class.getName());
        properties.put(Constants.VALUE_SERIALIZER_PROP_KEY, ByteArraySerializer.class.getName());

        // SASL properties
        properties.put(Constants.SASL_JAAS_CONFIG_PROP_KEY,
                String.format(Constants.SASL_JAAS_CONFIG_PROP_VALUE,
                        customTracingProperties.username(),
                        customTracingProperties.password()));
        properties.put(Constants.SASL_MECHANISM_CONFIG_PROP_KEY, Constants.SASL_MECHANISM_CONFIG_PROP_VALUE);

        // Security
        properties.put(Constants.SECURITY_PROTOCOL_PROP_KEY, Constants.SECURITY_PROTOCOL_PROP_VALUE);

        // Client Id
        final String serviceName = environment.getProperty(Constants.TRACING_SERVICE_NAME_PROP_KEY);
        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, serviceName);

        // Building sender with properties
        return KafkaSender
                .newBuilder()
                .topic(customTracingProperties.topic())
                .overrides(properties)
                .build();
    }
}
