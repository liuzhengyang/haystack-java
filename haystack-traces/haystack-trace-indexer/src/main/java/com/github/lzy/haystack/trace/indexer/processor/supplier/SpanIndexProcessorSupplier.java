package com.github.lzy.haystack.trace.indexer.processor.supplier;

import java.util.ArrayList;
import java.util.List;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.common.packer.NoopPacker;
import com.github.lzy.haystack.trace.indexer.processor.SpanIndexProcessor;
import com.github.lzy.haystack.trace.indexer.processor.StreamProcessor;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferMemoryStoreSupplier;
import com.github.lzy.haystack.trace.indexer.writer.TraceWriter;

/**
 * @author liuzhengyang
 */
public class SpanIndexProcessorSupplier implements StreamProcessorSupplier<String, Span> {
    private final List<TraceWriter> traceWriters;

    public SpanIndexProcessorSupplier(List<TraceWriter> traceWriterList) {
        this.traceWriters = traceWriterList;
    }
    public StreamProcessor<String, Span> get() {
        return new SpanIndexProcessor(new SpanBufferMemoryStoreSupplier(10, 10), traceWriters, new NoopPacker<>());
    }
}
