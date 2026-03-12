package ru.alfastrah.api.tracing.sender;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import ru.alfastrah.api.tracing.util.Constants;
import zipkin2.reporter.kafka.KafkaSender;

import java.util.Properties;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static ru.alfastrah.api.tracing.util.Constants.ZIPKIN_SENDER_BEAN_NAME;

/**
 * @author nosovni on 18.08.2023
 */
@SpringBootTest(
        classes = KafkaSenderConfiguration.class,
        properties = {
                "custom.tracing.bootstrap-servers=specified-server1,specified-server2",
                "custom.tracing.password=pass",
                "custom.tracing.username=user",
                "custom.tracing.topic=topic-for-traces",
                "custom.tracing.sasl-mechanism=some-mechanism",
                "custom.tracing.security-protocol=some-protocol",
                "spring.application.name=some-app"
        }
)
@ContextConfiguration(initializers = KafkaSenderConfigurationTest.Initializer.class)
class KafkaSenderConfigurationTest {

    @Qualifier(ZIPKIN_SENDER_BEAN_NAME)
    @Autowired
    private KafkaSender sender;

    @Test
    void shouldConfigureZipkinSender() {
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
                    then(properties.get(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG))
                            .asInstanceOf(LIST)
                            .hasSize(2)
                            .contains("specified-server1", "specified-server2");

                    then(properties.get(Constants.KEY_SERIALIZER_PROP_KEY))
                            .isEqualTo("org.apache.kafka.common.serialization.ByteArraySerializer");

                    then(properties.get(Constants.VALUE_SERIALIZER_PROP_KEY))
                            .isEqualTo("org.apache.kafka.common.serialization.ByteArraySerializer");

                    then(properties.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))
                            .isEqualTo("some-protocol");

                    then(properties.get(Constants.SASL_MECHANISM_CONFIG_PROP_KEY))
                            .isEqualTo("some-mechanism");

                    then(properties.get(Constants.SASL_JAAS_CONFIG_PROP_KEY))
                            .isEqualTo("org.apache.kafka.common.security.scram.ScramLoginModule required username=user password=pass;");

                    then(properties.get(CommonClientConfigs.CLIENT_ID_CONFIG))
                            .isEqualTo("some-app");

                    then(properties.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG))
                            .isEqualTo(false);
                });
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "management.tracing.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}