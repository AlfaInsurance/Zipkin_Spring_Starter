package ru.alfastrah.api.tracing;

import feign.micrometer.MicrometerObservationCapability;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.alfastrah.api.tracing.observability.kafka.KafkaObservationEnablerPostProcessor;
import ru.alfastrah.api.tracing.observability.soap.SoapClientCreationAspect;
import ru.alfastrah.api.tracing.observability.soap.SoapTraceParentOutDatabindingInterceptor;
import zipkin2.reporter.kafka.KafkaSender;

/**
 * @author nosovni on 29.10.2023
 */
@SpringBootTest(properties = "management.tracing.enabled=false")
class CustomTracingAutoConfigurationDisabledTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldNotConfigureSender() {
        Assertions.assertThatThrownBy(() -> context.getBean(KafkaSender.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessageContainingAll("No qualifying bean", "KafkaSender", "available");
    }

    @Test
    void shouldNotConfigureSoapClientCreationAspect() {
        Assertions.assertThatThrownBy(() -> context.getBean(SoapClientCreationAspect.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessageContainingAll("No qualifying bean", "SoapClientCreationAspect", "available");
    }

    @Test
    void shouldNotConfigureSoapTraceParentOutDatabindingInterceptor() {
        Assertions.assertThatThrownBy(() -> context.getBean(SoapTraceParentOutDatabindingInterceptor.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessageContainingAll("No qualifying bean", "SoapTraceParentOutDatabindingInterceptor", "available");
    }

    @Test
    void shouldNotConfigureKafkaObservationEnablerPostProcessor() {
        Assertions.assertThatThrownBy(() -> context.getBean(KafkaObservationEnablerPostProcessor.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessageContainingAll("No qualifying bean", "KafkaObservationEnablerPostProcessor", "available");
    }

    @Test
    void shouldNotConfigureMicrometerObservationCapability() {
        Assertions.assertThatThrownBy(() -> context.getBean(MicrometerObservationCapability.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessageContainingAll("No qualifying bean", "MicrometerObservationCapability", "available");
    }
}
