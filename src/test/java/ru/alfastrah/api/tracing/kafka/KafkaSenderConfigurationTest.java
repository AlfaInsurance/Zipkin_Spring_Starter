package ru.alfastrah.api.tracing.kafka;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka.KafkaSender;

import java.util.Properties;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * @author nosovni on 29.10.2023
 */
@SpringBootTest(
        classes = KafkaSenderConfiguration.class,
        properties = {
                "custom.tracing.bootstrap-servers=specified-server1,specified-server2",
                "custom.tracing.password=pass",
                "custom.tracing.username=user",
                "custom.tracing.topic=topic-for-traces",
                "spring.application.name=some-app"
        }
)
class KafkaSenderConfigurationTest {

    @Qualifier("zipkinSender")
    @Autowired
    private Sender sender;

    @Test
    void shouldConfigureSender() {
        Assertions.assertThat(sender)
                .isNotNull()
                .isInstanceOf(KafkaSender.class)
                .satisfies(kafkaSender ->
                        then(kafkaSender)
                                .extracting("topic")
                                .isEqualTo("topic-for-traces")
                )
                .extracting("properties")
                .isInstanceOfSatisfying(Properties.class, properties -> {
                    then(properties.get("bootstrap.servers"))
                            .asList()
                            .hasSize(2)
                            .contains("specified-server1", "specified-server2");

                    then(properties.get("key.serializer"))
                            .isEqualTo("org.apache.kafka.common.serialization.ByteArraySerializer");

                    then(properties.get("value.serializer"))
                            .isEqualTo("org.apache.kafka.common.serialization.ByteArraySerializer");

                    then(properties.get("security.protocol"))
                            .isEqualTo("SASL_PLAINTEXT");

                    then(properties.get("sasl.jaas.config"))
                            .isEqualTo("org.apache.kafka.common.security.scram.ScramLoginModule required username=user password=pass;");

                    then(properties.get("client.id"))
                            .isEqualTo("some-app");
                });
    }
}