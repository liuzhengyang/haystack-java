package com.github.lzy.haystack.trace.indexer.writer.es;

import java.io.IOException;
import java.util.Optional;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import com.alibaba.fastjson.JSON;
import com.expedia.open.tracing.buffer.SpanBuffer;
import com.github.lzy.haystack.trace.common.packer.PackedMessage;
import com.github.lzy.haystack.trace.indexer.writer.TraceWriter;

/**
 *
 */
public class ElasticSearchWriter implements TraceWriter {

    @Override
    public void writeAsync(String traceId, PackedMessage<SpanBuffer> packedSpanBuffer, boolean isLastSpanBuffer) {
        IndexDocumentGenerator indexDocumentGenerator = new IndexDocumentGenerator();
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        Optional<TraceIndexDoc> indexDocument = indexDocumentGenerator.createIndexDocument(traceId, packedSpanBuffer.getProtoObj());
        if (indexDocument.isPresent()) {
            IndexRequest indexRequest = new IndexRequest("span")
                    .id(traceId)
                    .source(JSON.toJSONString(indexDocument.get()), XContentType.JSON);
            try {
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // TODO Batch execute

    }

    @Override
    public void close() throws Exception {

    }
}
