package ru.alfastrah.api.tracing.observability.soap;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.alfastrah.api.tracing.observability.soap.ws.KnownSoapClient;
import utils.EnableTestObservation;
import utils.UnknownSoapClient;

/**
 * @author nosovni on 23.05.2025
 */
@EnableTestObservation
@ImportAutoConfiguration(SoapObservationConfiguration.class)
@Import(TestSoapClientsConfiguration.class)
@ExtendWith(SpringExtension.class)
class SoapObservationConfigurationTest {

    @Autowired
    private KnownSoapClient knownSoapClient;

    @Autowired
    private UnknownSoapClient unknownSoapClient;

    @Test
    void shouldAddTracingToKnownSoapClient() {
        final SoftAssertions softAssertions = new SoftAssertions();
        final Client client = ClientProxy.getClient(knownSoapClient);

        softAssertions.assertThat(knownSoapClient).isNotNull();
        softAssertions.assertThat(client.getOutInterceptors())
                .hasSize(1)
                .first()
                .isInstanceOf(SoapTraceParentOutDatabindingInterceptor.class);

        softAssertions.assertAll();
    }

    @Test
    void shouldNotAddTracingToUnknownClient() {
        final SoftAssertions softAssertions = new SoftAssertions();
        final Client client = ClientProxy.getClient(unknownSoapClient);

        softAssertions.assertThat(unknownSoapClient).isNotNull();
        softAssertions.assertThat(client.getOutInterceptors())
                .isEmpty();

        softAssertions.assertAll();
    }

}