package com.github.lzy.haystack.trace.indexer.writer.es;

import static com.github.lzy.haystack.trace.indexer.writer.es.TraceIndexDoc.DURATION_KEY_NAME;
import static com.github.lzy.haystack.trace.indexer.writer.es.TraceIndexDoc.OPERATION_KEY_NAME;
import static com.github.lzy.haystack.trace.indexer.writer.es.TraceIndexDoc.SERVICE_KEY_NAME;
import static com.github.lzy.haystack.trace.indexer.writer.es.TraceIndexDoc.START_TIME_KEY_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.expedia.open.tracing.Span;
import com.expedia.open.tracing.Tag;
import com.expedia.open.tracing.buffer.SpanBuffer;

public class IndexDocumentGenerator {

    public Optional<TraceIndexDoc> createIndexDocument(String traceId, SpanBuffer spanBuffer) {
        List<Map<String, Object>> spanIndices = new ArrayList<>();
        AtomicLong traceStartTime = new AtomicLong(Long.MAX_VALUE);
        AtomicLong rootDuration = new AtomicLong();

        spanBuffer.getChildSpansList()
                .stream()
                .filter(this::isValidForIndex)
                .forEach(span -> {
                    traceStartTime.set(Math.min(traceStartTime.get(), span.getStartTime()));
                    if (span.getParentSpanId() == null) {
                        rootDuration.set(span.getDuration());
                    }

                    Map<String, Object> spanIndexDoc = spanIndices.stream()
                            .filter(sp -> sp.get(OPERATION_KEY_NAME) == span.getOperationName() && sp.get(SERVICE_KEY_NAME) == span.getServiceName())
                            .findAny()
                            .orElseGet(() -> {
                                Map<String, Object> newSpanIndexDoc = new HashMap<>();
                                newSpanIndexDoc.put(SERVICE_KEY_NAME, span.getServiceName());
                                newSpanIndexDoc.put(OPERATION_KEY_NAME, span.getOperationName());
                                spanIndices.add(newSpanIndexDoc);
                                return newSpanIndexDoc;
                            });
                    updateSpanIndexDoc(spanIndexDoc, span);
                });
        return spanIndices.isEmpty() ? Optional.empty() : Optional.of(new TraceIndexDoc(traceId, rootDuration.get(), traceStartTime.get(), spanIndices));
    }

    private boolean isValidForIndex(Span span) {
        return StringUtils.isNotEmpty(span.getServiceName()) && StringUtils.isNotEmpty(span.getOperationName());
    }

    private void updateSpanIndexDoc(Map<String, Object> spanIndexDoc, Span span) {
        span.getTagsList()
                .forEach(tag -> {
                    String normalizedTagKey = tag.getKey().toLowerCase();
                    append(spanIndexDoc, tag.getKey(), readTagValue(tag));
                });
        append(spanIndexDoc, DURATION_KEY_NAME, span.getDuration());
        append(spanIndexDoc, START_TIME_KEY_NAME, span.getStartTime());
    }

    private void append(Map<String, Object> spanIndexDoc, String key, Object value) {
        HashSet<Object> hashSet = (HashSet<Object>) spanIndexDoc.computeIfAbsent(key, k -> new HashSet<>());
        hashSet.add(value);
    }

    private Object readTagValue(Tag tag) {
        switch (tag.getType()) {
            case BOOL:
                return tag.getVBool();
            case STRING:
                return tag.getVStr();
            case LONG:
                return tag.getVLong();
            case DOUBLE:
                return tag.getVDouble();
            case BINARY:
                return tag.getVBytes();
            default:
                throw new RuntimeException("Unknown tag type " + tag.getType());
        }
    }
}
