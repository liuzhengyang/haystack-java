package com.github.lzy.haystack.agent;

public interface Agent extends AutoCloseable {
    String getName();
    void start();
}
