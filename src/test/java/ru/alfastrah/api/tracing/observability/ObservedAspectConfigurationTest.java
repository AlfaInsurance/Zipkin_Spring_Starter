package ru.alfastrah.api.tracing.observability;

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
import utils.EnableTestObservation;

/**
 * @author nosovni on 18.08.2023
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
                .hasObservationWithNameEqualTo("something-that-method-does")
                .that()
                .hasBeenStarted()
                .hasBeenStopped();
    }

    @TestComponent
    public static class SomeClass {
        @Observed(name = "something-that-method-does")
        public void foo() {
            System.out.println("bar");
        }
    }
}