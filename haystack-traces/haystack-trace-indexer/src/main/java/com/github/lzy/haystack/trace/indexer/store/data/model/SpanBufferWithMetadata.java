package com.github.lzy.haystack.trace.indexer.store.data.model;

import com.expedia.open.tracing.buffer.SpanBuffer;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class SpanBufferWithMetadata {
    private SpanBuffer.Builder builder;
    private Long firstSpanSeenAt;
    private Long firstSeedSpanKafkaOffset;
}
