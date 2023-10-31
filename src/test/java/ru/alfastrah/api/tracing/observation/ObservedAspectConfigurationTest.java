package ru.alfastrah.api.tracing.observation;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import testutils.EnableTestObservation;

/**
 * @author nosovni on 29.10.2023
 */
@EnableTestObservation
@SpringBootTest(classes = ObservedAspectConfigurationTest.SomeClass.class)
@ImportAutoConfiguration(ZipkinAutoConfiguration.class)
class ObservedAspectConfigurationTest {

    @Autowired
    private SomeClass someClass;

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldObserve() {
        someClass.foo();

        final TestObservationRegistry observationRegistry = context.getBean(TestObservationRegistry.class);
        TestObservationRegistryAssert.assertThat(observationRegistry)
                .hasObservationWithNameEqualTo("observation-name")
                .that()
                .hasBeenStarted()
                .hasBeenStopped();
    }

    @TestComponent
    public static class SomeClass {
        @Observed(name = "observation-name")
        public void foo() {
            System.out.println("bar");
        }
    }
}