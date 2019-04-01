package com.github.lzy.haystack.trace.indexer.store;

import java.util.List;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.indexer.store.data.model.SpanBufferWithMetadata;

/**
 * @author liuzhengyang
 */
public interface SpanBufferKeyValueStore {

    List<SpanBufferWithMetadata> getAndRemoveSpanBuffersOlderThan(Long timestamp);

    void addEvictionListener(EldestBufferedSpanEvictionListener listener);

    SpanBufferWithMetadata addOrUpdateSpanBuffer(String traceId, Span span,
            Long spanRecordTimestamp, Long offset);

    void init();

    void close();


}
