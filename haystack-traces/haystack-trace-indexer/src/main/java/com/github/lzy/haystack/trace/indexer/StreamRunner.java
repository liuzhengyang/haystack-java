package com.github.lzy.haystack.trace.indexer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.indexer.processor.supplier.SpanIndexProcessorSupplier;
import com.github.lzy.haystack.trace.indexer.store.SpanBufferMemoryStoreSupplier;
import com.github.lzy.haystack.trace.indexer.writer.TraceWriter;

public class StreamRunner {

    private List<TraceWriter> traceWriters;

    public StreamRunner(List<TraceWriter> traceWriters) {
        this.traceWriters = traceWriters;
    }

    public void start() {
        System.out.println("Starting the span indexing stream...");

        SpanBufferMemoryStoreSupplier spanBufferMemoryStoreSupplier = new SpanBufferMemoryStoreSupplier(10, 10);
        SpanIndexProcessorSupplier spanIndexProcessorSupplier = new SpanIndexProcessorSupplier();
        Map<String, Object> kafkaProperties = new HashMap<>();
        kafkaProperties.put("bootstrap.servers", "127.0.0.1:9092");
        kafkaProperties.put("group.id", "spanIndexWriter");
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer", "com.github.lzy.haystack.trace.indexer.processor.SpanDeserializer");
        KafkaConsumer<String, Span> kafkaConsumer = new KafkaConsumer<>(kafkaProperties);
        kafkaConsumer.subscribe(Collections.singleton("spanDispatch"));
        while (true) {
            ConsumerRecords<String, Span> records = kafkaConsumer.poll(Duration.ofSeconds(1));
            if (!records.isEmpty()) {
                spanIndexProcessorSupplier.get().process(records.records("spanDispatch"));
                System.out.println("Process done");
            }
        }

    }
}
