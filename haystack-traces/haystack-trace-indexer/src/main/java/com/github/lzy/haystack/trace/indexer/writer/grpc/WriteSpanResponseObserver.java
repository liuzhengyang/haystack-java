package com.github.lzy.haystack.trace.indexer.writer.grpc;

import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.backend.WriteSpansResponse;

import io.grpc.stub.StreamObserver;

/**
 * @author liuzhengyang
 */
public class WriteSpanResponseObserver implements StreamObserver<WriteSpansResponse> {

    private static final Logger logger = LoggerFactory.getLogger(WriteSpanResponseObserver.class);

    private final Semaphore inflightRequest;

    public WriteSpanResponseObserver(Semaphore inflightRequest) {
        this.inflightRequest = inflightRequest;
    }

    @Override
    public void onNext(WriteSpansResponse writeSpansResponse) {
        inflightRequest.release();
    }

    @Override
    public void onError(Throwable throwable) {
        inflightRequest.release();
        logger.error("Fail to write trace-backend with exception", throwable);
    }

    @Override
    public void onCompleted() {

    }
}
