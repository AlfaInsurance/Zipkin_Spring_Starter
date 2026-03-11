package ru.alfastrah.api.tracing.observability.soap;

import io.micrometer.tracing.Tracer;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JaxWsProxyFactoryBean.class)
public class SoapObservationConfiguration {

    /**
     * Creates SOAP tracing interceptor that adds trace parent information to outgoing requests.
     * Enables distributed tracing for SOAP web service calls by injecting trace headers.
     *
     * @param tracer the tracer instance for creating and managing trace spans
     * @return configured SOAP tracing interceptor for outbound requests
     */
    @Bean
    @ConditionalOnBean(Tracer.class)
    SoapTraceParentOutDatabindingInterceptor soapTraceParentOutDatabindingInterceptor(
            Tracer tracer
    ) {
        return new SoapTraceParentOutDatabindingInterceptor(tracer);
    }

    /**
     * Creates AOP aspect for SOAP client creation that automatically applies tracing interceptor.
     * Intercepts SOAP client creation to inject tracing capabilities for distributed tracing.
     *
     * @param interceptor the tracing interceptor to be applied to SOAP clients
     * @return configured aspect for automatic SOAP client tracing setup
     */
    @Bean
    @ConditionalOnBean(SoapTraceParentOutDatabindingInterceptor.class)
    SoapClientCreationAspect soapTracingAspect(
            SoapTraceParentOutDatabindingInterceptor interceptor
    ) {
        return new SoapClientCreationAspect(interceptor);
    }
}
