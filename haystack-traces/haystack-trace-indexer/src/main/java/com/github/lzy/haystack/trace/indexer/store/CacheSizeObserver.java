package com.github.lzy.haystack.trace.indexer.store;

/**
 * @author liuzhengyang
 */
public interface CacheSizeObserver {
    void onCacheSizeChange(int maxEntries);
}
