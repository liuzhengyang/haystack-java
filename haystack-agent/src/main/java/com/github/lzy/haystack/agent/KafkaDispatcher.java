package com.github.lzy.haystack.agent;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaDispatcher implements Dispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDispatcher.class);

    private static final String TOPIC = "spanDispatch";

    private KafkaProducer<byte[], byte[]> producer;

    @Override
    public void dispatch(byte[] partitionKey, byte[] data) throws Exception {
        producer.send(new ProducerRecord<byte[], byte[]>(TOPIC, partitionKey, data));
    }

    @Override
    public void initialize() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        producer = new KafkaProducer<byte[], byte[]>(properties, new ByteArraySerializer(), new ByteArraySerializer());
        LOGGER.info("Successfully initialized the kafka dispatcher with config={}", properties);
    }

    @Override
    public void close() throws Exception {

    }
}
