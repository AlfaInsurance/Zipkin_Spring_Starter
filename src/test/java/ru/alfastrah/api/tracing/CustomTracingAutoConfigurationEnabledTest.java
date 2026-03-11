package ru.alfastrah.api.tracing;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.aop.ObservedAspect;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import ru.alfastrah.api.tracing.observability.kafka.KafkaObservationEnablerPostProcessor;
import ru.alfastrah.api.tracing.observability.soap.SoapClientCreationAspect;
import ru.alfastrah.api.tracing.observability.soap.SoapTraceParentOutDatabindingInterceptor;
import utils.EnableTestObservation;
import zipkin2.reporter.kafka.KafkaSender;

/**
 * @author nosovni on 29.10.2023
 */
@SpringBootTest(
        properties = {
                "custom.tracing.bootstrap-servers=specified-server1,specified-server2",
                "custom.tracing.password=pass",
                "custom.tracing.username=user",
                "custom.tracing.topic=topic-for-traces",
                "custom.tracing.sasl-mechanism=SCRAM-SHA-256",
                "custom.tracing.security-protocol=SASL_SSL",
                "spring.application.name=some-app"
        }
)
@ImportAutoConfiguration(CustomTracingAutoConfiguration.class)
@EnableTestObservation
@ContextConfiguration(initializers = CustomTracingAutoConfigurationEnabledTest.Initializer.class)
class CustomTracingAutoConfigurationEnabledTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldConfigureSender() {
        Assertions.assertThat(applicationContext.getBean(KafkaSender.class))
                .isNotNull()
                .isInstanceOf(KafkaSender.class);
    }

    @Test
    void shouldConfigureObservation() {
        Assertions.assertThat(applicationContext.getBean(ObservedAspect.class))
                .isNotNull();
    }

    @Test
    void shouldConfigureMicrometerObservationCapability() {
        Assertions.assertThat(applicationContext.getBean(MicrometerObservationCapability.class))
                .isNotNull();
    }

    @Test
    void shouldConfigureKafkaObservationEnablerPostProcessor() {
        Assertions.assertThat(applicationContext.getBean(KafkaObservationEnablerPostProcessor.class))
                .isNotNull();
    }

    @Test
    void shouldConfigureSoapClientCreationAspect() {
        Assertions.assertThat(applicationContext.getBean(SoapClientCreationAspect.class))
                .isNotNull();
    }

    @Test
    void shouldConfigureSoapTraceParentOutDatabindingInterceptor() {
        Assertions.assertThat(applicationContext.getBean(SoapTraceParentOutDatabindingInterceptor.class))
                .isNotNull();
    }


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "management.tracing.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}