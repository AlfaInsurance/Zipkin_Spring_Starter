package ru.alfastrah.api.tracing;

import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import ru.alfastrah.api.tracing.kafka.KafkaSenderConfiguration;
import ru.alfastrah.api.tracing.observation.ObservedAspectConfiguration;
import ru.alfastrah.api.tracing.util.Constants;

/**
 * @author nosovni on 29.10.2023
 */
@AutoConfiguration
@ConditionalOnProperty(name = Constants.TRACING_ENABLED_NAME_PROP_KEY, havingValue = "true")
@AutoConfigureBefore(ZipkinAutoConfiguration.class)
@Import({KafkaSenderConfiguration.class, ObservedAspectConfiguration.class})
public class MyTracingAutoConfiguration {
}