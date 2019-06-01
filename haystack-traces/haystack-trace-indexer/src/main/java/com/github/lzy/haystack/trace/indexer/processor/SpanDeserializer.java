package com.github.lzy.haystack.trace.indexer.processor;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.expedia.open.tracing.Span;
import com.google.protobuf.InvalidProtocolBufferException;

public class SpanDeserializer implements Deserializer<Span> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Span deserialize(String topic, byte[] data) {
        try {
            return Span.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
