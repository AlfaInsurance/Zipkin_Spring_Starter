package ru.alfastrah.api.tracing.observability.feign;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.micrometer.observation.annotation.Observed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.bind.annotation.GetMapping;
import utils.EnableTestObservation;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = FeignClientTraceTest.TestConfig.class,
        properties = {
                "management.tracing.enabled=true",
                "management.tracing.sampling.probability=1",
                "wiremock.server.baseUrl=http://localhost:${wiremock.server.port}",
                "spring.cloud.openfeign.client.config.default.loggerLevel=full",
                "spring.cloud.openfeign.client.config.default.connectTimeout=5000",
                "spring.cloud.openfeign.client.config.default.readTimeout=5000"
        })
@EnableTestObservation
@ImportAutoConfiguration({FeignAutoConfiguration.class, ZipkinAutoConfiguration.class, BraveAutoConfiguration.class})
@Import({FeignObservedConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@AutoConfigureWireMock(port = 0)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FeignClientTraceTest {

    @Autowired
    private TestConfig.TestFeignClient testFeignClient;

    /**
     * Checks, that traceparent http-header successfully added to the request.
     */
    @Test
    void testTraceparentHeaderAddedByTracing() {
        givenThat(WireMock.get(WireMock.urlEqualTo("/external"))
                .withHeader("traceparent",
                        matching("00-.*-.*-01")) // traceparent pattern
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Hello from WireMock!")
                ));

        assertThat(testFeignClient.getExternalData())
                .isNotNull()
                .isEqualTo("Hello from WireMock!");
    }

    /**
     * FeignClient for test
     */
    @Configuration
    @EnableFeignClients(clients = FeignClientTraceTest.TestConfig.TestFeignClient.class)
    static class TestConfig {
        @FeignClient(name = "testClient", url = "${wiremock.server.baseUrl}")
        @Observed
        interface TestFeignClient {

            @GetMapping("/external")
            String getExternalData();
        }
    }
}