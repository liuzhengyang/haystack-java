package com.github.lzy.haystack.trace.indexer.store;

/**
 * @author liuzhengyang
 */
public interface StoreSupplier<K> {
    K get();
}
