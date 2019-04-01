package com.github.lzy.haystack.trace.indexer.writer;

import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.backend.WriteSpansResponse;

import io.grpc.stub.StreamObserver;

/**
 * @author liuzhengyang
 */
public class WriteSpanResponseObserver implements StreamObserver<WriteSpansResponse> {

//    private Semaphore

    private static final Logger logger = LoggerFactory.getLogger(WriteSpanResponseObserver.class);

    @Override
    public void onNext(WriteSpansResponse writeSpansResponse) {
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
