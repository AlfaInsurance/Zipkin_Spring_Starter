package ru.alfastrah.api.tracing.observability.reactor;

import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import utils.EnableTestObservation;

@WebFluxTest(
        properties = {
                "management.tracing.enabled=true",
                "custom.tracing.bootstrap-servers=specified-server1,specified-server2",
                "custom.tracing.password=pass",
                "custom.tracing.username=user",
                "custom.tracing.topic=topic-for-traces",
                "custom.tracing.sasl-mechanism=some-mechanism",
                "custom.tracing.security-protocol=some-protocol",
                "spring.application.name=some-app"
        }
)
@ImportAutoConfiguration(ReactorObservationConfiguration.class)
@EnableTestObservation
class ReactorObservationConfigurationEnabledTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldConfigureReactorObservationInitializer() {
        final TestObservationRegistry registry = applicationContext.getBean(TestObservationRegistry.class);
        Assertions.assertThat(ObservationThreadLocalAccessor.getInstance().getObservationRegistry())
                .isSameAs(registry);

        Assertions.assertThat(applicationContext.getBeansOfType(org.springframework.beans.factory.InitializingBean.class))
                .containsKey("reactorObservationInitializer");
    }

}
