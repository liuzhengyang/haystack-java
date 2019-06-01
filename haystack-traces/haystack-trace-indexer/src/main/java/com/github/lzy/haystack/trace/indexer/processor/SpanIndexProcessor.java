package com.github.lzy.haystack.trace.indexer.processor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.Span;
import com.expedia.open.tracing.buffer.SpanBuffer;
import com.github.lzy.haystack.trace.common.packer.PackedMessage;
import com.github.lzy.haystack.trace.common.packer.Packer;
import com.github.lzy.haystack.trace.indexer.store.EldestBufferedSpanEvictionListener;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferKeyValueStore;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferMemoryStoreSupplier;
import com.github.lzy.haystack.trace.indexer.store.data.model.SpanBufferWithMetadata;
import com.github.lzy.haystack.trace.indexer.writer.TraceWriter;

/**
 * @author liuzhengyang
 */
public class SpanIndexProcessor implements StreamProcessor<String, Span>, EldestBufferedSpanEvictionListener {
    private static final Logger logger = LoggerFactory.getLogger(SpanIndexProcessor.class);

    private static final long pollIntervalMillis = 10000;
    private static final long bufferingWindowMillis = 10000;

    private final SpanBufferMemoryStoreSupplier storeSupplier;
    private final List<TraceWriter> writers;
    private final Packer<SpanBuffer> spanBufferPacker;

    private SpanBufferKeyValueStore spanBufferMemStore;

    private Long lastEmitTimestamp = 0L;

    public SpanIndexProcessor(SpanBufferMemoryStoreSupplier storeSupplier, List<TraceWriter> writers, Packer<SpanBuffer> spanBufferPacker) {
        this.storeSupplier = storeSupplier;
        this.writers = writers;
        this.spanBufferPacker = spanBufferPacker;
        spanBufferMemStore = storeSupplier.get();
    }

    @Override
    public Optional<OffsetAndMetadata> process(Iterable<ConsumerRecord<String, Span>> records) {
        try {
            AtomicLong currentTimestamp = new AtomicLong();
            AtomicLong minEventTime = new AtomicLong(Long.MAX_VALUE);

            records.forEach(record -> {
                if (record != null) {
                    spanBufferMemStore.addOrUpdateSpanBuffer(record.key(), record.value(),
                            record.timestamp(), record.offset());
                    currentTimestamp.set(Math.max(record.timestamp(), currentTimestamp.get()));

                    if (record.value().getStartTime() > 0) {
                        minEventTime.set(Math.min(record.value().getStartTime(), minEventTime.get()));
                    }
                }
            });
            mayBeEmit(currentTimestamp.get());
        } catch (Exception e) {
            logger.error("Fail to process records", e);
        }
        return Optional.empty();
    }

    private Optional<OffsetAndMetadata> mayBeEmit(long currentTimestamp) {
        if (currentTimestamp - pollIntervalMillis > lastEmitTimestamp) {
            long committableOffset = -1L;

            List<SpanBufferWithMetadata> emittableSpanBuffers = spanBufferMemStore.getAndRemoveSpanBuffersOlderThan(currentTimestamp - 0);

            System.out.println("emittable buffers" + emittableSpanBuffers);
            for (int i = 0; i < emittableSpanBuffers.size(); i++) {
                SpanBufferWithMetadata buffer = emittableSpanBuffers.get(i);
                SpanBuffer spanBuffer = buffer.getBuilder().build();
                writeTrace(spanBuffer, i == emittableSpanBuffers.size() - 1);
                if (committableOffset < buffer.getFirstSeedSpanKafkaOffset()) {
                    committableOffset = buffer.getFirstSeedSpanKafkaOffset();
                }
            }

            lastEmitTimestamp = currentTimestamp;

            if (committableOffset >= 0) {
                return Optional.of(new OffsetAndMetadata(committableOffset));
            }
        }
        return Optional.empty();
    }

    @Override
    public void close() {
        spanBufferMemStore.close();
        logger.info("Span Index Processor has been closed now!");
    }

    @Override
    public void init() {
        spanBufferMemStore = storeSupplier.get();
        spanBufferMemStore.init();
        spanBufferMemStore.addEvictionListener(this);
        logger.info("Span Index Processor has been initialized successfully!");
    }

    @Override
    public void onEvict(String key, SpanBufferWithMetadata value) {
        writeTrace(value.getBuilder().build(), false);
    }

    private void writeTrace(SpanBuffer spanBuffer, boolean isLastSpanBuffer) {
        String traceId = spanBuffer.getTraceId();
        PackedMessage<SpanBuffer> packedMessage = spanBufferPacker.apply(spanBuffer);
        writers.forEach(writer -> {
            writer.writeAsync(traceId, packedMessage, isLastSpanBuffer);
        });
    }
}
