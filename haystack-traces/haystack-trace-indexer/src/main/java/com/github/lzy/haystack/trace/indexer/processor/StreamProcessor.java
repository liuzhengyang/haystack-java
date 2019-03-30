package com.github.lzy.haystack.trace.indexer.processor;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;

/**
 * @author liuzhengyang
 */
public interface StreamProcessor<K, V> {

    Optional<OffsetAndMetadata> process(Iterable<ConsumerRecord<K, V>> record);

    void close();

    void init();
}
