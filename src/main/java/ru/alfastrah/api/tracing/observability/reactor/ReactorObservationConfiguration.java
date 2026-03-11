package ru.alfastrah.api.tracing.observability.reactor;

import io.micrometer.context.ContextRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactorObservationConfiguration {

    /**
     * Initializes Reactor observation capabilities by enabling automatic context propagation
     * and linking Micrometer's ThreadLocal with Reactor Context for tracing support.
     *
     * @param observationRegistry the observation registry for metrics and tracing
     * @return initializing bean that configures Reactor observation on startup
     */
    @Bean
    public InitializingBean reactorObservationInitializer(
            ObservationRegistry observationRegistry
    ) {
        return () -> {
            // Link ThreadLocal Micrometer with Reactor Context
            final ObservationThreadLocalAccessor threadLocalAccessor = ObservationThreadLocalAccessor.getInstance();
            threadLocalAccessor.setObservationRegistry(observationRegistry);
            ContextRegistry.getInstance().registerThreadLocalAccessor(threadLocalAccessor);

            // Enable automatic context propagation in Reactor 3.5.3+
            Hooks.enableAutomaticContextPropagation();
        };
    }
}

