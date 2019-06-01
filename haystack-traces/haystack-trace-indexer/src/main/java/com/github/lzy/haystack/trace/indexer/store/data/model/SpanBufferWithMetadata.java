package com.github.lzy.haystack.trace.indexer.store.data.model;

import com.expedia.open.tracing.buffer.SpanBuffer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
@AllArgsConstructor
public class SpanBufferWithMetadata {
    private SpanBuffer.Builder builder;
    private Long firstSpanSeenAt;
    private Long firstSeedSpanKafkaOffset;
}
