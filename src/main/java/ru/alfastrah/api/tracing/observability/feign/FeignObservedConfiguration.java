package ru.alfastrah.api.tracing.observability.feign;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class FeignObservedConfiguration {

    /**
     * Creates Micrometer observation capability for Feign clients to enable tracing.
     * Adds {@code traceparent} HTTP header to outgoing requests for distributed tracing.
     *
     * @param registry the observation registry for metrics and tracing
     * @return configured Micrometer observation capability for Feign
     */
    @Bean
    public MicrometerObservationCapability micrometerObservationCapability(
            ObservationRegistry registry
    ) {
        return new MicrometerObservationCapability(registry);
    }
}
