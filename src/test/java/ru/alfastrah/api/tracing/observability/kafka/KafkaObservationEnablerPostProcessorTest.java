package ru.alfastrah.api.tracing.observability.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaObservationEnablerPostProcessorTest {

    @InjectMocks
    private KafkaObservationEnablerPostProcessor postProcessor;

    @Mock
    private ConcurrentKafkaListenerContainerFactory<String, String> containerFactory;
    @Mock
    private ContainerProperties containerProperties;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldEnableObservationForKafkaConsumer() {
        when(containerFactory.getContainerProperties()).thenReturn(containerProperties);

        assertThat(postProcessor.postProcessAfterInitialization(containerFactory, "testConsumerFactory"))
                .isNotNull()
                .isEqualTo(containerFactory);

        verify(containerFactory).getContainerProperties();
        verify(containerProperties).setObservationEnabled(true);
        verifyNoMoreInteractions(containerFactory, containerProperties);
    }

    @Test
    void shouldEnableObservationForKafkaProducer() {
        assertThat(postProcessor.postProcessAfterInitialization(kafkaTemplate, "testKafkaTemplate"))
                .isNotNull()
                .isEqualTo(kafkaTemplate);

        verify(kafkaTemplate).setObservationEnabled(true);
        verifyNoMoreInteractions(kafkaTemplate);
        verifyNoInteractions(containerFactory);
    }

    @Test
    void shouldNotModifyOtherBeans() {
        Object otherBean = new Object();

        assertThat(postProcessor.postProcessAfterInitialization(otherBean, "otherBean"))
                .isNotNull()
                .isEqualTo(otherBean);

        verifyNoInteractions(containerProperties, kafkaTemplate);
    }
}
