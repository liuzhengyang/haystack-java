package com.github.lzy.haystack.trace.storage.backend.memory;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lzy.haystack.trace.storage.backend.memory.service.SpansPersistenceService;
import com.github.lzy.haystack.trace.storage.backend.memory.store.InMemoryTraceRecordsStore;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

/**
 * @author liuzhengyang
 */
public class Service {
    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    public static void main(String[] args) {
        startService();
    }

    public static void startService() {
        try {

            InMemoryTraceRecordsStore store = new InMemoryTraceRecordsStore();
            Server server = NettyServerBuilder.forPort(8090)
                    .directExecutor()
                    .addService(new SpansPersistenceService(store))
                    .build();

            server.start();
            logger.info("Server started, listening on {}", server.getPort());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("shutting down gRPC server since JVM is shutting down");
                server.shutdown();
            }));

            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            logger.error("Fatal error observed while running", e);
            System.exit(1);
        }

    }
}
