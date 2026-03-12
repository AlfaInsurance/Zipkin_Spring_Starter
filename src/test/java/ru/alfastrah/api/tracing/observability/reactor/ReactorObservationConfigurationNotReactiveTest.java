package ru.alfastrah.api.tracing.observability.reactor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import utils.EnableTestObservation;

@SpringBootTest(
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
@EnableTestObservation
class ReactorObservationConfigurationNotReactiveTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldNotConfigureReactorObservationInitializerWhenNotReactiveWebApplication() {
        Assertions.assertThat(applicationContext.getBeansOfType(InitializingBean.class))
                .doesNotContainKey("reactorObservationInitializer");
    }

}
