package com.github.lzy.haystack.trace.indexer.store;

import com.github.lzy.haystack.trace.indexer.store.data.model.SpanBufferWithMetadata;

/**
 * @author liuzhengyang
 */
public interface EldestBufferedSpanEvictionListener {
    void onEvict(String key, SpanBufferWithMetadata value);
}
