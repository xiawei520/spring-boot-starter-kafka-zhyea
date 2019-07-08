package com.elderbyte.kafka.consumer.factory;

import com.elderbyte.kafka.consumer.configuration.AutoOffsetReset;
import com.elderbyte.kafka.consumer.processing.Processor;
import com.elderbyte.kafka.metrics.MetricsContext;
import com.elderbyte.kafka.serialisation.SpringKafkaJsonDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.annotation.InterfaceStability;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.List;

/**
 * Provides the ability to build a kafka listener configuration
 */
@InterfaceStability.Evolving
public interface KafkaListenerBuilder<K,V> {

    /***************************************************************************
     *                                                                         *
     * Builder API                                                             *
     *                                                                         *
     **************************************************************************/

    <NV> KafkaListenerBuilder<K,NV> jsonValue(Class<NV> valueClazz);

    <NK> KafkaListenerBuilder<NK,V> jsonKey(Class<NK> keyClazz);

    <NV> KafkaListenerBuilder<K,NV> jsonValue(TypeReference<NV> valueTypeRef);

    <NK> KafkaListenerBuilder<NK,V> jsonKey(TypeReference<NK> keyTypeRef);

    KafkaListenerBuilder<K,String> stringValue();

    KafkaListenerBuilder<String,V> stringKey();

    <NV> KafkaListenerBuilder<K,NV> valueDeserializer(Deserializer<NV> valueDeserializer);

    <NK> KafkaListenerBuilder<NK,V> keyDeserializer(Deserializer<NK> keyDeserializer);

    KafkaListenerBuilder<K,V>  autoOffsetReset(AutoOffsetReset autoOffsetReset);

    KafkaListenerBuilder<K,V> failIfTopicsAreMissing(boolean value);

    KafkaListenerBuilder<K,V> consumerGroup(String groupId);

    KafkaListenerBuilder<K,V> consumerId(String clientId);

    KafkaListenerBuilder<K,V> metrics(MetricsContext metricsContext);

    /**
     * Skip records on error
     */
    KafkaListenerBuilder<K,V> ignoreErrors();

    /**
     * On error, retry the given number of times.
     * After that, a errorhandler is invoked and the records are skipped.
     */
    KafkaListenerBuilder<K,V> blockingRetries(int retries);

    /**
     * Enable / Disable auto-commit. Default is false to support error handling.
     */
    KafkaListenerBuilder<K, V> autoCommit(boolean autoCommit);


    /**
     * Callback for rebalance handler.
     *
     * @deprecated Will be removed once supported natively by the builder
     */
    @InterfaceStability.Unstable
    KafkaListenerBuilder<K, V> rebalanceListener(ConsumerAwareRebalanceListener rebalanceListener); // TODO custom builder

    /**
     * Set whether or not to call consumer.commitSync() or commitAsync() when the
     * container is responsible for commits. Default true.
     */
    KafkaListenerBuilder<K, V> syncCommits(boolean syncCommits);

    /**
     * Set the max time to block in the consumer waiting for records.
     */
    KafkaListenerBuilder<K, V> pollTimeout(long pollTimeout);


    /***************************************************************************
     *                                                                         *
     * Builder end API                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * @deprecated Please switch to build(processor).start();
     */
    @Deprecated
    default void startProcess(Processor<ConsumerRecord<K, V>> processor) {
        build(processor).start();
    }

    /**
     * @deprecated Please switch to buildBatch(processor).start();
     */
    @Deprecated
    default void startProcessBatch(Processor<List<ConsumerRecord<K, V>>> processor){
        buildBatch(processor).start();
    }

    /**
     * Build a single record listener from this builder configuration.
     */
    MessageListenerContainer build(Processor<ConsumerRecord<K, V>> processor);

    /**
     * Build a kafka batch listener from this builder configuration.
     */
    MessageListenerContainer buildBatch(Processor<List<ConsumerRecord<K, V>>> processor);
}
