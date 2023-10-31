package ru.alfastrah.api.tracing.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author nosovni on 29.10.2023
 */
public class Constants {

    public static final String BOOTSTRAP_SERVERS_PROP_KEY = "bootstrap.servers";
    public static final String KEY_SERIALIZER_PROP_KEY = "key.serializer";
    public static final String VALUE_SERIALIZER_PROP_KEY = "value.serializer";
    public static final String SASL_JAAS_CONFIG_PROP_KEY = "sasl.jaas.config";
    public static final String TRACING_SERVICE_NAME_PROP_KEY = "spring.application.name";
    public static final String TRACING_ENABLED_NAME_PROP_KEY = "management.tracing.enabled";
    public static final String SASL_MECHANISM_CONFIG_PROP_KEY = "sasl.mechanism";
    public static final String SECURITY_PROTOCOL_PROP_KEY = "security.protocol";

    public static final String SASL_JAAS_CONFIG_PROP_VALUE = "org.apache.kafka.common.security.scram.ScramLoginModule" +
            " required username=%s password=%s;";

    public static final String SASL_MECHANISM_CONFIG_PROP_VALUE = "SCRAM-SHA-256";
    public static final String SECURITY_PROTOCOL_PROP_VALUE = "SASL_PLAINTEXT";

    public static final String ZIPKIN_SENDER_BEAN_NAME = "zipkinSender";

    private Constants() {
    }

    public static class Helpers {
        public static final BiFunction<String, String, List<String>> STRING_TO_LIST = (sequence, delimiter) ->
                Arrays.stream(sequence.split(delimiter))
                        .map(String::trim)
                        .toList();

        private Helpers() {
        }
    }

}
