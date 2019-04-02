package com.github.lzy.haystack.trace.indexer.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.open.tracing.Span;
import com.github.lzy.haystack.trace.indexer.processor.supplier.StreamProcessorSupplier;

/**
 * @author liuzhengyang
 */
public class StreamTaskRunnable implements StreamProcessorSupplier<String, Span>, Runnable,
        AutoCloseable {

    private static final String CONSUMER_TOPIC = "consumerTopic";

    private final ConcurrentMap<TopicPartition, StreamProcessor<String, Span>> processors
            = new ConcurrentHashMap<>();

    private final List<StateListener> listeners = new ArrayList<>();
    private final AtomicBoolean shutdownRequest = new AtomicBoolean(false);
    private final ExecutorService wakeupScheduler = new ScheduledThreadPoolExecutor(1);

    private final RebalanceListener rebalanceListener = new RebalanceListener();

    private volatile StreamTaskState state = StreamTaskState.NOT_RUNNING;
    private int wakeups = 0;

    private final int taskId;
    private final StreamProcessorSupplier<String, Span> processorSupplier;

    public StreamTaskRunnable(int taskId, StreamProcessorSupplier<String, Span> processorSupplier) {
        this.taskId = taskId;
        this.processorSupplier = processorSupplier;
        consumer().subscribe(Collections.singletonList(CONSUMER_TOPIC), rebalanceListener);
    }

    private KafkaConsumer<String, Span> consumer() {
        Properties properties = new Properties();
        return new KafkaConsumer<>(properties);
    }

    private static final Logger logger = LoggerFactory.getLogger(StreamTaskRunnable.class);

    private class RebalanceListener implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {
            logger.info("Partitions {} revoked at the begging of consumer rebalance for task");

            collection.forEach(c -> {
                StreamProcessor<String, Span> processor = processors.remove(c);
                if (processor != null) {
                    processor.close();
                }
            });
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {
            logger.info("Partitions {} assigned at the begging of consumer rebalance for task {}"
                    , collection, taskId);

            collection.forEach(partition -> {
                StreamProcessor<String, Span> processor = processorSupplier.get();
                StreamProcessor<String, Span> previous = processors.putIfAbsent(partition, processor);
                if (previous == null) {
                    processor.init();
                }
            });
        }
    }

    @Override
    public StreamProcessor<String, Span> get() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void run() {
        logger.info("Starting stream processing thread with id {}", taskId);
        try {

        } catch (Exception e) {

        } finally {

        }
    }
}
