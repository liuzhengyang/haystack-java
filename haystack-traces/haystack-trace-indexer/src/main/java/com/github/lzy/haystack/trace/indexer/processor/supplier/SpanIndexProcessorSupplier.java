package com.github.lzy.haystack.trace.indexer.processor.supplier;

import java.util.ArrayList;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.common.packer.NoopPacker;
import com.github.lzy.haystack.trace.indexer.processor.SpanIndexProcessor;
import com.github.lzy.haystack.trace.indexer.processor.StreamProcessor;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferMemoryStoreSupplier;

/**
 * @author liuzhengyang
 */
public class SpanIndexProcessorSupplier implements StreamProcessorSupplier<String, Span> {
    public StreamProcessor<String, Span> get() {
        return new SpanIndexProcessor(new SpanBufferMemoryStoreSupplier(10, 10), new ArrayList<>(), new NoopPacker<>());
    }
}
