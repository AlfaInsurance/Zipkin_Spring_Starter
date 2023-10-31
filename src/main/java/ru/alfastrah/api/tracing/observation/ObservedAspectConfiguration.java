package ru.alfastrah.api.tracing.observation;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configures ObservedAspect to add probability of collecting traces for the concrete
 * method (or all of the methods of the class)
 *
 * @author nosovni on 29.10.2023
 */
@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class ObservedAspectConfiguration {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
