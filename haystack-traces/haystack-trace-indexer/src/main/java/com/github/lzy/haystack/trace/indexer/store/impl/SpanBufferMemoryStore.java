package com.github.lzy.haystack.trace.indexer.store.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.indexer.store.CacheSizeObserver;
import com.github.lzy.haystack.trace.indexer.store.DynamicCacheSizer;
import com.github.lzy.haystack.trace.indexer.store.EldestBufferedSpanEvictionListener;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferKeyValueStore;
import com.github.lzy.haystack.trace.indexer.store.data.model.SpanBufferWithMetadata;

/**
 * @author liuzhengyang
 */
public class SpanBufferMemoryStore implements SpanBufferKeyValueStore, CacheSizeObserver {
    private static final Logger logger = LoggerFactory.getLogger(SpanBufferMemoryStore.class);

    private final DynamicCacheSizer dynamicCacheSizer;

    private volatile boolean open = false;

    private AtomicInteger maxEntries = new AtomicInteger(10000);
    private List<EldestBufferedSpanEvictionListener> listeners = new ArrayList<>();
    private int totalSpansInMemStore = 0;
    private Map<String, SpanBufferWithMetadata> map = new LinkedHashMap<>();

    public SpanBufferMemoryStore(DynamicCacheSizer dynamicCacheSizer) {
        this.dynamicCacheSizer = dynamicCacheSizer;
    }

    @Override
    public List<SpanBufferWithMetadata> getAndRemoveSpanBuffersOlderThan(Long timestamp) {

        return null;
    }

    @Override
    public void addEvictionListener(EldestBufferedSpanEvictionListener listener) {

    }

    @Override
    public SpanBufferWithMetadata addOrUpdateSpanBuffer(String traceId, Span span, Long spanRecordTimestamp, Long offset) {
        return null;
    }

    @Override
    public void init() {
        dynamicCacheSizer.addCacheObserver(this);

        map = new LinkedHashMap<String, SpanBufferWithMetadata>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, SpanBufferWithMetadata> eldest) {
                boolean evict = totalSpansInMemStore >= maxEntries.get();
                if (evict) {
                    totalSpansInMemStore -= eldest.getValue().getBuilder().getChildSpansCount();
                    listeners.forEach(listener -> listener.onEvict(eldest.getKey(), eldest.getValue()));
                }
                return evict;
            }
        };

        open = true;

        logger.info("Span buffer memory store has been initialized");
    }

    @Override
    public void close() {

    }

    @Override
    public void onCacheSizeChange(int maxEntries) {

    }
}
