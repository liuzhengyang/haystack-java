package com.github.lzy.haystack.trace.indexer.processor.supplier;

import com.github.lzy.haystack.trace.indexer.processor.StreamProcessor;

/**
 * @author liuzhengyang
 */
public interface StreamProcessorSupplier<K, V> {
    StreamProcessor<K, V> get();
}
