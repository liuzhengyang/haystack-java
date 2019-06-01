package com.github.lzy.haystack.agent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.github.lzy.haystack.agent.service.SpanAgentGrpcService;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

public class SpanAgent implements Agent {

    private Dispatcher dispatcher;

    public SpanAgent() {
    }

    @Override
    public void start() {
        dispatcher = new KafkaDispatcher();
        dispatcher.initialize();
        Server server = NettyServerBuilder
                .forPort(34000)
                .directExecutor()
                .permitKeepAliveWithoutCalls(true)
                .permitKeepAliveTime(1, TimeUnit.DAYS)
                .addService(new SpanAgentGrpcService(dispatcher))
                .build();
        try {
            server.start();
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
