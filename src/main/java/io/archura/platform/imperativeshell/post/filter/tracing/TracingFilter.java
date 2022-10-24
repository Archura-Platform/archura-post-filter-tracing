package io.archura.platform.imperativeshell.post.filter.tracing;

import io.archura.platform.api.context.Context;
import io.archura.platform.api.http.HttpServerRequest;
import io.archura.platform.api.http.HttpServerResponse;
import io.archura.platform.api.logger.Logger;
import io.archura.platform.api.type.Configurable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Objects.nonNull;


public class TracingFilter implements BiConsumer<HttpServerRequest, HttpServerResponse>, Configurable {

    public static final String SPAN_HEADER_NAME = "X-A-Span-ID";
    private Map<String, Object> configuration = new HashMap<>();

    @Override
    public void accept(final HttpServerRequest request, final HttpServerResponse response) {
        final Context context = (Context) request.getAttributes().get(Context.class.getSimpleName());
        final Logger logger = context.getLogger();

        final String spanHeaderName = String.valueOf(configuration.getOrDefault("SpanHeaderName", SPAN_HEADER_NAME));
        final String spanId = request.getFirstHeader(spanHeaderName);
        if (nonNull(spanId)) {
            logger.debug("Span header: '%s', value: '%s'", spanHeaderName, spanId);
            response.getHeaders().putIfAbsent(spanHeaderName, List.of(spanId));
        }
    }

    @Override
    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }
}
