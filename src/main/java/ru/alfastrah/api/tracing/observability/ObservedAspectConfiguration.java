package ru.alfastrah.api.tracing.observability;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class ObservedAspectConfiguration {

    /**
     * Creates AOP aspect that handles {@code @Observed} annotation for method-level tracing.
     * Enables automatic observation collection for annotated methods and classes.
     *
     * @param observationRegistry the observation registry for metrics and tracing
     * @return configured aspect for processing @Observed annotations
     */
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
