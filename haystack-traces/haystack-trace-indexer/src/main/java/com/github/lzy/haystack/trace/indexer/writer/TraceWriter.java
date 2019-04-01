package com.github.lzy.haystack.trace.indexer.writer;

import com.expedia.open.tracing.buffer.SpanBuffer;
import com.github.lzy.haystack.trace.common.packer.PackedMessage;

/**
 * @author liuzhengyang
 */
public interface TraceWriter extends AutoCloseable {

    void writeAsync(String traceId, PackedMessage<SpanBuffer> packedSpanBuffer,
            boolean isLastSpanBuffer);
}
