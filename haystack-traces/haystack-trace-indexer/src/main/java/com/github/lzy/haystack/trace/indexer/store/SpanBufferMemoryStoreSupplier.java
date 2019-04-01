package com.github.lzy.haystack.trace.indexer.store;

import com.github.lzy.haystack.trace.indexer.store.impl.SpanBufferMemoryStore;

/**
 * @author liuzhengyang
 */
public class SpanBufferMemoryStoreSupplier implements StoreSupplier<SpanBufferKeyValueStore> {

    private final int minTracesPerCache;
    private final int maxEntriesAcrossStores;

    private final DynamicCacheSizer dynamicCacheSizer;

    public SpanBufferMemoryStoreSupplier(int minTracesPerCache, int maxEntriesAcrossStores) {
        this.minTracesPerCache = minTracesPerCache;
        this.maxEntriesAcrossStores = maxEntriesAcrossStores;
        dynamicCacheSizer = new DynamicCacheSizer(minTracesPerCache, maxEntriesAcrossStores);
    }

    @Override
    public SpanBufferKeyValueStore get() {
        return new SpanBufferMemoryStore(dynamicCacheSizer);
    }
}
