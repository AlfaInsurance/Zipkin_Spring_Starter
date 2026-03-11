package ru.alfastrah.api.tracing;

import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import ru.alfastrah.api.tracing.observability.ObservedAspectConfiguration;
import ru.alfastrah.api.tracing.observability.feign.FeignObservedConfiguration;
import ru.alfastrah.api.tracing.observability.kafka.KafkaObservationEnablerConfig;
import ru.alfastrah.api.tracing.observability.reactor.ReactorObservationConfiguration;
import ru.alfastrah.api.tracing.observability.soap.SoapObservationConfiguration;
import ru.alfastrah.api.tracing.sender.KafkaSenderConfiguration;
import ru.alfastrah.api.tracing.util.Constants;

@AutoConfiguration(before = ZipkinAutoConfiguration.class)
@ConditionalOnProperty(name = Constants.TRACING_ENABLED_NAME_PROP_KEY, havingValue = "true")
@Import({
        KafkaSenderConfiguration.class,
        ObservedAspectConfiguration.class,
        FeignObservedConfiguration.class,
        KafkaObservationEnablerConfig.class,
        SoapObservationConfiguration.class,
        ReactorObservationConfiguration.class
})
public class CustomTracingAutoConfiguration {
}