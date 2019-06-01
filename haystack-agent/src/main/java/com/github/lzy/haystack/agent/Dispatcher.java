package com.github.lzy.haystack.agent;

public interface Dispatcher extends AutoCloseable {
    void dispatch(final byte[] partitionKey, final byte[] data) throws Exception;
    void initialize();
}
