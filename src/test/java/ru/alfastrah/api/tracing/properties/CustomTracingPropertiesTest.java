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
                "custom.tracing.topic=topic"
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
                .satisfies(props -> {
                    Assertions.assertThat(props.username())
                            .isNotBlank()
                            .isEqualTo("user");
                    Assertions.assertThat(props.password())
                            .isNotBlank()
                            .isEqualTo("pass");
                    Assertions.assertThat(props.topic())
                            .isNotBlank()
                            .isEqualTo("topic");
                    Assertions.assertThat(props.bootstrapServers())
                            .isNotEmpty()
                            .contains("server1", "server2");
                });
    }

    @ConfigurationPropertiesScan(basePackageClasses = CustomTracingProperties.class)
    @TestConfiguration
    static class TestConfig {
    }

}