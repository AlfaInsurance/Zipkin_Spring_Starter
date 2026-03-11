package ru.alfastrah.api.tracing.observability.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;


@Slf4j
public class KafkaObservationEnablerPostProcessor implements BeanPostProcessor {

    /**
     * Automatically enables observation for Kafka components after bean initialization.
     * Processes both consumer factories and producer templates to enable tracing.
     *
     * @param bean     the bean instance to process
     * @param beanName the name of the bean
     * @return the original bean instance
     * @throws BeansException if an error occurs during processing
     */
    @Override
    public Object postProcessAfterInitialization(
            @NonNull Object bean,
            @NonNull String beanName
    ) throws BeansException {
        if (bean instanceof ConcurrentKafkaListenerContainerFactory<?, ?> factory) {
            factory.getContainerProperties().setObservationEnabled(true);
            log.debug("Observation enabled in consumer [beanName: {}, class: {}]", beanName, bean.getClass());

        } else if (bean instanceof KafkaTemplate<?, ?> kafkaTemplate) {
            kafkaTemplate.setObservationEnabled(true);
            log.debug("Observation enabled in producer [beanName: {}, class: {}]", beanName, bean.getClass());

        }
        return bean;
    }
}
