package com.github.lzy.haystack.trace.indexer.processor;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.indexer.store.EldestBufferedSpanEvictionListener;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferKeyValueStore;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferMemoryStoreSupplier;
import com.github.lzy.haystack.trace.indexer.store.data.model.SpanBufferWithMetadata;

/**
 * @author liuzhengyang
 */
public class SpanIndexProcessor implements StreamProcessor<String, Span>, EldestBufferedSpanEvictionListener {
    private static final Logger logger = LoggerFactory.getLogger(SpanIndexProcessor.class);

    private final SpanBufferMemoryStoreSupplier storeSupplier;
    private final SpanAcc

    private SpanBufferKeyValueStore spanBufferMemStore;

    private Long lastEmitTimestamp = 0L;

    @Override
    public Optional<OffsetAndMetadata> process(Iterable<ConsumerRecord<String, Span>> record) {
        return Optional.empty();
    }

    @Override
    public void close() {
        spanBufferMemStore.close();
        logger.info("Span Index Processor has been closed now!");
    }

    @Override
    public void init() {
        spanBufferMemStore = store
    }

    @Override
    public void onEvict(String key, SpanBufferWithMetadata value) {

    }
}
