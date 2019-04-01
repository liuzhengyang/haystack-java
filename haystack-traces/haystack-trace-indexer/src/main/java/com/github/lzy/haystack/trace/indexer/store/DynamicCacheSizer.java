package com.github.lzy.haystack.trace.indexer.store;

import java.util.HashSet;
import java.util.Set;

/**
 * @author liuzhengyang
 */
public class DynamicCacheSizer {
    private final int minTracesPerCache;
    private final int maxEntriesAcrossCaches;

    private Set<CacheSizeObserver> cacheObservers = new HashSet<>();

    public DynamicCacheSizer(int minTracesPerCache, int maxEntriesAcrossCaches) {
        this.minTracesPerCache = minTracesPerCache;
        this.maxEntriesAcrossCaches = maxEntriesAcrossCaches;
    }

    public void addCacheObserver(CacheSizeObserver observer) {
        synchronized (this) {
            cacheObservers.add(observer);
            evaluateNewCacheSizeAndNotify(cacheObservers);
        }
    }

    public void removeCacheObserver(CacheSizeObserver observer) {
        synchronized (this) {
            cacheObservers.remove(observer);
            evaluateNewCacheSizeAndNotify(cacheObservers);
        }
    }

    private void evaluateNewCacheSizeAndNotify(Set<CacheSizeObserver> observers) {
        if (!observers.isEmpty()) {
            int newMaxEntriesPerCache = (int) Math.floor(maxEntriesAcrossCaches * 1.0 / observers.size());
            observers.forEach(obs -> obs.onCacheSizeChange(newMaxEntriesPerCache));
        }
    }
}
