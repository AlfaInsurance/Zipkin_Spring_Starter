package ru.alfastrah.api.tracing.observability.soap;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.alfastrah.api.tracing.util.Constants.HttpHeaders.TRACE_PARENT;

/**
 * @author nosovni on 22.05.2025
 */
@SpringBootTest(classes = BraveAutoConfiguration.class)
class SoapTraceParentOutDatabindingInterceptorTest {

    public static final String PARENT_ID = "00f067aa0ba902b7";
    public static final String TRACE_ID = "682f21ed69be1e85384d56a74b586754";
    public static final String SPAN_ID = "e67b56031ba19311";
    @SpyBean
    private Tracer tracer;

    @MockBean
    private CurrentTraceContext currentTraceContext;

    private SoapTraceParentOutDatabindingInterceptor interceptor;

    public static Stream<Arguments> provideMessages() {
        final MessageImpl messageWithoutHeaders = new MessageImpl();

        final MessageImpl messageWithHeaders = new MessageImpl();
        final Map<String, List<String>> headers = new TreeMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_XML_VALUE));
        messageWithHeaders.put(Message.PROTOCOL_HEADERS, headers);

        final MessageImpl messageWithEmptyHeaders = new MessageImpl();
        messageWithEmptyHeaders.put(Message.PROTOCOL_HEADERS, new TreeMap<>());

        return Stream.of(
                messageWithoutHeaders,
                messageWithHeaders,
                messageWithEmptyHeaders
        ).map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        interceptor = new SoapTraceParentOutDatabindingInterceptor(tracer);
        when(tracer.currentTraceContext())
                .thenReturn(currentTraceContext);
    }

    @ParameterizedTest
    @MethodSource("provideMessages")
    void handleMessage_shouldAddTraceParentHeader(Message message) {
        final TraceContext mockedTraceContext = getTraceContext();

        when(currentTraceContext.context())
                .thenReturn(mockedTraceContext);

        @SuppressWarnings("unchecked") final Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        final int initialHeaderMapSize = isNull(headers) ? 0 : headers.size();

        interceptor.handleMessage(message);

        final StringJoiner expectedTraceParentValue = new StringJoiner("-");
        expectedTraceParentValue.add("00");
        expectedTraceParentValue.add(TRACE_ID);
        expectedTraceParentValue.add(PARENT_ID);
        expectedTraceParentValue.add("01");

        assertThat(message.get(Message.PROTOCOL_HEADERS))
                .asInstanceOf(MAP)
                .hasSize(initialHeaderMapSize + 1)
                .hasEntrySatisfying(TRACE_PARENT, headerValue ->
                        assertThat(headerValue)
                                .asInstanceOf(LIST)
                                .hasSize(1)
                                .first()
                                .isEqualTo(expectedTraceParentValue.toString())
                );

        verify(currentTraceContext).context();
        verifyNoMoreInteractions(currentTraceContext);
    }

    @Test
    void handleMessage_shouldNotAddTraceParentHeader_whenNoTraceContextAvailable() {
        when(currentTraceContext.context())
                .thenReturn(null);

        final MessageImpl mockedMessage = mock(MessageImpl.class);
        interceptor.handleMessage(mockedMessage);

        verifyNoInteractions(mockedMessage);
        verify(currentTraceContext).context();
        verifyNoMoreInteractions(currentTraceContext);
    }

    private TraceContext getTraceContext() {
        return tracer.traceContextBuilder()
                .parentId(PARENT_ID)
                .traceId(TRACE_ID)
                .spanId(SPAN_ID)
                .sampled(true)
                .build();
    }
}