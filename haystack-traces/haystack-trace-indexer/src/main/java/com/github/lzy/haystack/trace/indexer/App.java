package com.github.lzy.haystack.trace.indexer;

import java.util.ArrayList;
import java.util.List;

import com.github.lzy.haystack.trace.indexer.writer.TraceWriter;
import com.github.lzy.haystack.trace.indexer.writer.es.ElasticSearchWriter;
import com.github.lzy.haystack.trace.indexer.writer.grpc.GrpcTraceWriter;

/**
 * @author liuzhengyang
 */
public class App {
    public static void main(String[] args) {
        List<TraceWriter> traceWriterList = new ArrayList<>();
        traceWriterList.add(new ElasticSearchWriter());
        traceWriterList.add(new GrpcTraceWriter());
        StreamRunner streamRunner = new StreamRunner(traceWriterList);
        streamRunner.start();
    }
}
