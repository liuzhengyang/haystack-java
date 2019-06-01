package com.github.lzy.haystack.trace.indexer.writer.es;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraceIndexDoc {
    public static final String SERVICE_KEY_NAME = "servicename";
    public static final String OPERATION_KEY_NAME = "operationname";
    public static final String DURATION_KEY_NAME = "duration";
    public static final String START_TIME_KEY_NAME = "starttime";
    private String traceId;
    private Long rootDuration;
    private Long startTime;
    private List<Map<String, Object>> spans;
}
