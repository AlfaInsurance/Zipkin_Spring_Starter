package ru.alfastrah.api.tracing.observability.feign;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(classes = FeignObservedConfiguration.class)
@MockBean({ObservationRegistry.class})
class FeignObservedConfigurationTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldCreateMicrometerObservationCapability() {
        MicrometerObservationCapability capability = applicationContext.getBean(MicrometerObservationCapability.class);

        assertThat(capability)
                .isNotNull()
                .isInstanceOf(MicrometerObservationCapability.class);
    }
}
