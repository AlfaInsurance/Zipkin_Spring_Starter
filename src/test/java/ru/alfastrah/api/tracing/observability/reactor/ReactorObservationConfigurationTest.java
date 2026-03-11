package ru.alfastrah.api.tracing.observability.reactor;

import io.micrometer.context.ContextRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Hooks;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
class ReactorObservationConfigurationTest {

    private MockedStatic<Hooks> hooksMock;
    private MockedStatic<ContextRegistry> contextRegistryMock;
    private MockedStatic<ObservationThreadLocalAccessor> accessorMock;

    @MockBean
    private ObservationRegistry observationRegistry;
    @MockBean
    private ContextRegistry contextRegistryInstance;
    @MockBean
    private ObservationThreadLocalAccessor accessorInstance;

    @BeforeEach
    void setUp() {
        hooksMock = mockStatic(Hooks.class);
        contextRegistryMock = mockStatic(ContextRegistry.class);
        accessorMock = mockStatic(ObservationThreadLocalAccessor.class);

        contextRegistryMock.when(ContextRegistry::getInstance)
                .thenReturn(contextRegistryInstance);
        accessorMock.when(ObservationThreadLocalAccessor::getInstance)
                .thenReturn(accessorInstance);
    }

    @AfterEach
    void tearDown() {
        Hooks.disableAutomaticContextPropagation();

        hooksMock.close();
        contextRegistryMock.close();
        accessorMock.close();
    }

    @Test
    void reactorObservationInitializer_shouldRegisterAccessorAndEnablePropagation() throws Exception {
        final ReactorObservationConfiguration config = new ReactorObservationConfiguration();
        final InitializingBean initializer = config.reactorObservationInitializer(observationRegistry);

        initializer.afterPropertiesSet();

        verify(contextRegistryInstance).registerThreadLocalAccessor(accessorInstance);
        verify(accessorInstance).setObservationRegistry(observationRegistry);
        hooksMock.verify(Hooks::enableAutomaticContextPropagation);
        verifyNoMoreInteractions(contextRegistryInstance, accessorInstance);
        verifyNoInteractions(observationRegistry);
    }
}