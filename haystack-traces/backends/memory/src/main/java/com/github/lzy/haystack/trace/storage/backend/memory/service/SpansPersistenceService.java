package com.github.lzy.haystack.trace.storage.backend.memory.service;

import java.util.List;

import com.expedia.open.tracing.backend.ReadSpansRequest;
import com.expedia.open.tracing.backend.ReadSpansResponse;
import com.expedia.open.tracing.backend.StorageBackendGrpc;
import com.expedia.open.tracing.backend.TraceRecord;
import com.expedia.open.tracing.backend.WriteSpansRequest;
import com.expedia.open.tracing.backend.WriteSpansResponse;
import com.github.lzy.haystack.trace.storage.backend.memory.store.InMemoryTraceRecordsStore;

import io.grpc.stub.StreamObserver;

/**
 * @author liuzhengyang
 */
public class SpansPersistenceService extends StorageBackendGrpc.StorageBackendImplBase {

    private InMemoryTraceRecordsStore store;

    public SpansPersistenceService(InMemoryTraceRecordsStore store) {
        this.store = store;
    }

    @Override
    public void writeSpans(WriteSpansRequest request, StreamObserver<WriteSpansResponse> responseObserver) {
        store.writeTraceRecords(request.getRecordsList());
        WriteSpansResponse response = WriteSpansResponse.newBuilder()
                .setCode(WriteSpansResponse.ResultCode.SUCCESS)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readSpans(ReadSpansRequest request, StreamObserver<ReadSpansResponse> responseObserver) {
        List<TraceRecord> traceRecords = store.readTraceRecords(request.getTraceIdsList());
        ReadSpansResponse readSpansResponse = ReadSpansResponse.newBuilder()
                .addAllRecords(traceRecords)
                .build();
        responseObserver.onNext(readSpansResponse);
        responseObserver.onCompleted();
    }
}
