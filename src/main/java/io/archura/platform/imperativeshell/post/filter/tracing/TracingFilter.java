package io.archura.platform.imperativeshell.post.filter.tracing;

import io.archura.platform.api.attribute.TraceKeys;
import io.archura.platform.api.context.Context;
import io.archura.platform.api.http.HttpServerRequest;
import io.archura.platform.api.http.HttpServerResponse;
import io.archura.platform.api.logger.Logger;
import io.archura.platform.api.tracer.Tracer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TracingFilter implements BiConsumer<HttpServerRequest, HttpServerResponse> {
    private final List<String> traceKeys = Arrays.stream(TraceKeys.values()).map(TraceKeys::getKey).toList();

    @Override
    public void accept(final HttpServerRequest request, final HttpServerResponse response) {
        final Context context = (Context) request.getAttributes().get(Context.class.getSimpleName());
        final Logger logger = context.getLogger();
        final Optional<Tracer> tracerOptional = context.getTracer();
        if (tracerOptional.isPresent()) {
            final Map<String, Object> traceMap = request.getAttributes().entrySet().stream()
                    .filter(e -> traceKeys.contains(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            final Tracer tracer = tracerOptional.get();
            tracer.trace(traceMap);
        } else {
            logger.debug("The tracing filter is configured but the tracer is not available.");
        }
    }

}
