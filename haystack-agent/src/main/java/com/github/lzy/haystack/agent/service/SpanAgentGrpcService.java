package com.github.lzy.haystack.agent.service;

import java.nio.charset.StandardCharsets;

import com.expedia.open.tracing.Span;
import com.expedia.open.tracing.agent.api.DispatchResult;
import com.expedia.open.tracing.agent.api.SpanAgentGrpc;
import com.github.lzy.haystack.agent.Dispatcher;

import io.grpc.stub.StreamObserver;

public class SpanAgentGrpcService extends SpanAgentGrpc.SpanAgentImplBase {

    private Dispatcher dispatcher;

    public SpanAgentGrpcService(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void dispatch(Span request, StreamObserver<DispatchResult> responseObserver) {
        try {
            dispatcher.dispatch(request.getTraceId().getBytes(StandardCharsets.UTF_8), request.toByteArray());
        } catch (Exception e) {
            // FIXME exception handle
            e.printStackTrace();
        }
        DispatchResult result = DispatchResult.newBuilder().setCode(DispatchResult.ResultCode.SUCCESS).build();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
