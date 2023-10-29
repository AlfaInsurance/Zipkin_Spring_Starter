package ru.alfastrah.api.tracing;

import io.micrometer.observation.aop.ObservedAspect;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import testutils.EnableTestObservation;
import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka.KafkaSender;

/**
 * @author nosovni on 29.10.2023
 */
@SpringBootTest(
        classes = MyTracingAutoConfiguration.class,
        properties = {
                "custom.tracing.bootstrap-servers=specified-server1,specified-server2",
                "custom.tracing.password=pass",
                "custom.tracing.username=user",
                "custom.tracing.topic=topic-for-traces",
                "spring.application.name=some-app",
                "management.tracing.enabled=true"
        }
)
@EnableTestObservation
class MyTracingAutoConfigurationEnabledTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldConfigureSender() {
        Assertions.assertThat(applicationContext.getBean(Sender.class))
                .isNotNull()
                .isInstanceOf(KafkaSender.class);
    }

    @Test
    void shouldConfigureObservation() {
        Assertions.assertThat(applicationContext.getBean(ObservedAspect.class))
                .isNotNull();
    }
}