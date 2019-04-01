package com.github.lzy.haystack.trace.common.utils;

import java.util.Optional;

import com.expedia.open.tracing.Log;
import com.expedia.open.tracing.Span;
import com.expedia.open.tracing.Tag;

/**
 * @author liuzhengyang
 */
public class SpanUtils {
    public static final String URL_TAG_KEY = "url";
    public static final String LOG_EVENT_TAG_KEy = "event";
    public static final String SERVER_SEND_EVENT = "ss";
    public static final String SERVER_RECV_EVENT = "sr";
    public static final String CLIENT_SEND_EVENT = "cs";
    public static final String CLIENT_RECV_EVENT = "cr";

    public static final String SPAN_KIND_TAG_KEY = "span.kind";
    public static final String SERVICE_TAG_KEY = "service";
    public static final String SERVER_SPAN_KIND = "server";
    public static final String CLIENT_SPAN_KIND = "client";


    public long getEventTimestamp(Span span, String event) {
        return span.getLogsList()
                .stream()
                .filter(log -> log.getFieldsList().stream().anyMatch(tag -> LOG_EVENT_TAG_KEy.equalsIgnoreCase(tag.getKey()) && tag.getVStr().equalsIgnoreCase(event)))
                .findFirst().map(Log::getTimestamp).orElse(0L);
    }

    public long getEndTime(Span span) {
        return span.getStartTime() + span.getDuration();
    }

    public boolean isMergeSpan(Span span) {
        return containsClientLogTag(span);
    }

    public boolean containsClientLogTag(Span span) {
        return containsLogTag(span, CLIENT_SEND_EVENT) && containsLogTag(span, CLIENT_RECV_EVENT);
    }

    public boolean containsServerTag(Span span) {
        return containsLogTag(span, SERVER_RECV_EVENT) && containsLogTag(span, SERVER_SEND_EVENT);
    }

    public boolean containsLogTag(Span span, String event) {
        return span.getLogsList()
                .stream()
                .anyMatch(log -> log.getFieldsList().stream().anyMatch(tag -> LOG_EVENT_TAG_KEy.equalsIgnoreCase(tag.getKey()) && tag.getVStr().equalsIgnoreCase(event)));
    }

    public Optional<Tag> getServiceTag(Span span) {
        return span.getTagsList()
                .stream()
                .filter(tag -> SERVICE_TAG_KEY.equalsIgnoreCase(tag.getKey()))
                .findFirst();

    }
}
