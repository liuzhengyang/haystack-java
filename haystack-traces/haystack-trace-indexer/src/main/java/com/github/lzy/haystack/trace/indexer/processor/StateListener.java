package com.github.lzy.haystack.trace.indexer.processor;

/**
 * @author liuzhengyang
 */
public interface StateListener {
    void onTaskStateChange(StreamTaskState state);
}

enum StreamTaskState {
    NOT_RUNNING,
    RUNNING,
    FAILED,
    CLOSED;
}
