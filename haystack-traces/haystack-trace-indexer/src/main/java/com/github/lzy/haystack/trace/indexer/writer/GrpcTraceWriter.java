package com.github.lzy.haystack.trace.indexer.writer;

import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.backend.StorageBackendGrpc;
import com.expedia.open.tracing.backend.TraceRecord;
import com.expedia.open.tracing.backend.WriteSpansRequest;
import com.expedia.open.tracing.buffer.SpanBuffer;
import com.github.lzy.haystack.trace.common.packer.PackedMessage;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.val;

/**
 * @author liuzhengyang
 */
public class GrpcTraceWriter implements TraceWriter {

    private static final Logger logger = LoggerFactory.getLogger(GrpcTraceWriter.class);

    private final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8090)
            .usePlaintext()
            .build();

    private final StorageBackendGrpc.StorageBackendStub client =
            StorageBackendGrpc.newStub(channel);

    private final Semaphore inflightRequestsSemaphore = new Semaphore(100, true);

    public GrpcTraceWriter() {

    }

    @Override
    public void writeAsync(String traceId, PackedMessage<SpanBuffer> packedSpanBuffer, boolean isLastSpanBuffer) {
        boolean isSemaphoreAcquired = false;

        try {
            inflightRequestsSemaphore.acquire();
            isSemaphoreAcquired = true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void execute(String traceId, PackedMessage<SpanBuffer> packedSpanBuffer) {
        TraceRecord.Builder singleRecord = TraceRecord.newBuilder()
                .setTraceId(traceId)
                .setTimestamp(System.currentTimeMillis())
                .setSpans(ByteString.copyFrom(packedSpanBuffer.packedDataBytes()));

        WriteSpansRequest writeSpansRequest = WriteSpansRequest.newBuilder().addRecords(singleRecord).build();

        client.writeSpans(writeSpansRequest, new WriteSpanResponseObserver());
    }

    @Override
    public void close() throws Exception {
        logger.info("Closing backend client now..");
        channel.shutdown();
    }
}
