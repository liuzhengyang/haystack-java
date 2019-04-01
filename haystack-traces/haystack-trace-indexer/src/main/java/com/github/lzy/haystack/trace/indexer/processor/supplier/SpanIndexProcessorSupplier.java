package com.github.lzy.haystack.trace.indexer.processor.supplier;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.indexer.processor.StreamProcessor;

/**
 * @author liuzhengyang
 */
public class SpanIndexProcessorSupplier implements StreamProcessorSupplier<String, Span> {


    public StreamProcessor<String, Span> get() {
        return ;
    }
}
