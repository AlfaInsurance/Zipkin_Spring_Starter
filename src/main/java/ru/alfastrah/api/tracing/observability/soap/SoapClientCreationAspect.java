package ru.alfastrah.api.tracing.observability.soap;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AOP aspect that automatically applies tracing interceptor to SOAP client beans.
 * Intercepts SOAP client creation in Spring configuration classes and adds
 * tracing capabilities for distributed tracing support.
 */
@Aspect
@RequiredArgsConstructor
public class SoapClientCreationAspect {

    private final SoapTraceParentOutDatabindingInterceptor soapTraceParentOutDatabindingInterceptor;

    /**
     * Pointcut matching SOAP client proxy creation methods.
     * Targets methods returning SOAP client types in the ru.alfastrah.api..ws.* package.
     */
    @Pointcut("execution(ru.alfastrah.api..ws.* *(..))")
    public void matchesSoapClientType() {
    }

    /**
     * Pointcut matching bean creation methods in Spring configuration classes.
     * Targets methods annotated with {@link Bean} within classes annotated with {@link Configuration}.
     */
    @Pointcut("@within(org.springframework.context.annotation.Configuration) && @annotation(org.springframework.context.annotation.Bean)")
    public void beanInConfiguration() {
    }

    /**
     * Combined pointcut for SOAP client bean creation.
     * Matches SOAP client beans created in Spring configuration classes.
     */
    @Pointcut("beanInConfiguration() && matchesSoapClientType()")
    public void soapClientBeanCreation() {
    }

    /**
     * Automatically adds tracing interceptor to newly created SOAP client beans.
     * Executed after SOAP client bean creation to inject {@link SoapTraceParentOutDatabindingInterceptor}
     * for distributed tracing support.
     *
     * @param proxy the newly created SOAP client proxy
     * @return the same proxy with tracing interceptor added
     */
    @AfterReturning(pointcut = "soapClientBeanCreation()", returning = "proxy")
    public Object afterUnicusSoapClientBeanCreated(Object proxy) {
        final Client client = ClientProxy.getClient(proxy);
        client.getOutInterceptors().add(soapTraceParentOutDatabindingInterceptor);
        return proxy;
    }
}
