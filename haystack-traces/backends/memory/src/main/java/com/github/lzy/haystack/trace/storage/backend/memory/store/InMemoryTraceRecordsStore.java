package com.github.lzy.haystack.trace.storage.backend.memory.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.backend.TraceRecord;

/**
 * @author liuzhengyang
 */
public class InMemoryTraceRecordsStore {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryTraceRecordsStore.class);

    private final Map<String, List<TraceRecord>> inMemoryTraceRecords = new HashMap<>();

    public List<TraceRecord> readTraceRecords(List<String> traceIds) {
        try {
            return traceIds.stream()
                    .flatMap(id -> inMemoryTraceRecords.getOrDefault(id,
                            Collections.emptyList()).stream()).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Failed to read raw traces with exception", ex);
            return Collections.emptyList();
        }
    }

    public void writeTraceRecords(List<TraceRecord> traceRecords) {
        traceRecords.forEach(traceRecord -> {
            try {
                List<TraceRecord> existingRecords =
                        inMemoryTraceRecords.getOrDefault(traceRecord.getTraceId(), new ArrayList<>());
                existingRecords.add(traceRecord);
                inMemoryTraceRecords.put(traceRecord.getTraceId(), existingRecords);
            } catch (Exception ex) {
                logger.error("Fail to write the spans to memory with exception", ex);
            }
        });
    }
}
