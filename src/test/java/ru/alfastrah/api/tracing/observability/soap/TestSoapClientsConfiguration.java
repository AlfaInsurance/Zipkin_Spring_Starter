package ru.alfastrah.api.tracing.observability.soap;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alfastrah.api.tracing.observability.soap.ws.KnownSoapClient;
import utils.UnknownSoapClient;

@Configuration
class TestSoapClientsConfiguration {

    public static final String TEST_URL = "test-url";

    @Bean
    public KnownSoapClient knownSoapClient() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(TEST_URL);
        return jaxWsProxyFactoryBean.create(KnownSoapClient.class);
    }

    @Bean
    public UnknownSoapClient unknownSoapClient() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(TEST_URL);
        return jaxWsProxyFactoryBean.create(UnknownSoapClient.class);
    }
}
