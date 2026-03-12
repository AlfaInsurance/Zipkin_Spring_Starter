package ru.alfastrah.api.tracing.observability.soap;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.apache.cxf.interceptor.AbstractOutDatabindingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import ru.alfastrah.api.tracing.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * SOAP interceptor that injects trace parent information into outgoing requests.
 * Extends CXF's AbstractOutDatabindingInterceptor to add distributed tracing headers
 * to SOAP messages during the PREPARE_SEND phase.
 */
public class SoapTraceParentOutDatabindingInterceptor extends AbstractOutDatabindingInterceptor {

    private final Tracer tracer;

    /**
     * Creates a new SOAP tracing interceptor that operates in the PREPARE_SEND phase.
     *
     * @param tracer the tracer instance for accessing current trace context
     */
    public SoapTraceParentOutDatabindingInterceptor(
            Tracer tracer
    ) {
        super(Phase.PREPARE_SEND);
        this.tracer = tracer;
    }

    /**
     * Retrieves or creates HTTP headers map from the SOAP message.
     * If no headers exist, creates a new TreeMap to store them.
     *
     * @param message the SOAP message to extract headers from
     * @return mutable map of HTTP headers
     */
    private static Map<String, List<String>> fetchHeaders(Message message) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        if (isNull(headers)) {
            headers = new TreeMap<>();
        }
        return headers;
    }

    /**
     * Intercepts outgoing SOAP messages to inject trace parent header for distributed tracing.
     * Extracts current trace context and adds traceparent header to enable trace propagation
     * across service boundaries.
     *
     * @param message the outgoing SOAP message to be processed
     * @throws Fault if an error occurs during message processing
     */
    @Override
    public void handleMessage(Message message) throws Fault {
        final TraceContext traceContext = tracer.currentTraceContext().context();
        if (nonNull(traceContext)) {
            final Map<String, List<String>> headers = fetchHeaders(message);
            headers.put(
                    Constants.HttpHeaders.TRACE_PARENT,
                    Constants.Helpers.TO_TRACE_PARENT.apply(traceContext)
            );
            message.put(Message.PROTOCOL_HEADERS, headers);
        }
    }
}
