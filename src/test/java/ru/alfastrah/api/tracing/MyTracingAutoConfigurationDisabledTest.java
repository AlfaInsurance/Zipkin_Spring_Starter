package ru.alfastrah.api.tracing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import zipkin2.reporter.kafka.KafkaSender;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * @author nosovni on 29.10.2023
 */
@SpringBootTest(properties = "management.tracing.enabled=false")
class MyTracingAutoConfigurationDisabledTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldNotConfigureSender() {
        assertThatThrownBy(() -> context.getBean(KafkaSender.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No qualifying bean of type 'zipkin2.reporter.kafka.KafkaSender' available");
    }
}
