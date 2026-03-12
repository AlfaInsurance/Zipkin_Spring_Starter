package ru.alfastrah.api.tracing.properties;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;


/**
 * @author nosovni on 29.10.2023
 */
@SpringBootTest(
        properties = {
                "custom.tracing.bootstrap-servers=server1,server2",
                "custom.tracing.password=pass",
                "custom.tracing.username=user",
                "custom.tracing.topic=topic",
                "custom.tracing.security-protocol=protocol",
                "custom.tracing.sasl-mechanism=mechanism"
        }
)
@EnableConfigurationProperties(CustomTracingProperties.class)
class CustomTracingPropertiesTest {

    @Autowired
    private CustomTracingProperties properties;

    @Test
    void should_FillProps() {
        Assertions.assertThat(properties)
                .isNotNull()
                .extracting(
                        CustomTracingProperties::username,
                        CustomTracingProperties::password,
                        CustomTracingProperties::topic,
                        CustomTracingProperties::bootstrapServers,
                        CustomTracingProperties::securityProtocol,
                        CustomTracingProperties::saslMechanism
                ).containsExactly(
                        "user",
                        "pass",
                        "topic",
                        "server1,server2",
                        "protocol",
                        "mechanism"
                );
    }

    @ConfigurationPropertiesScan(basePackageClasses = CustomTracingProperties.class)
    @TestConfiguration
    static class TestConfig {
    }

}