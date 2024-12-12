package ru.t1.school.open.project.application.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.serialization.MassageDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${t1.kafka.bootstrap.servers}")
    private String servers;
    @Value("${t1.kafka.topic.task}")
    private String taskTopic;
    @Value("${t1.kafka.consumer.group-id}")
    private String groupId;
    @Value("${t1.kafka.consumer.session.timeout-ms}")
    private String sessionTimeoutMs;
    @Value("${t1.kafka.consumer.heartbeat-interval-ms}")
    private String heartbeatIntervalMs;
    @Value("${t1.kafka.consumer.max-partition-fetch-bytes}")
    private String maxPartitionsFetchBytes;
    @Value("${t1.kafka.consumer.poll-timeout-ms}")
    private long pollTimeoutMs;
    @Value("${t1.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${t1.kafka.consumer.max-poll-interval-ms}")
    private String maxPollIntervalMs;

    @Bean
    public ConsumerFactory<String, TaskDto> taskKafkaConsumerListenerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_DOC, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MassageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.school.open.project.api.dto.TaskDto");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, Boolean.FALSE);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionsFetchBytes);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        DefaultKafkaConsumerFactory<String, TaskDto> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(pollTimeoutMs);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TaskDto> taskKafkaListenerContainerFactory(@Qualifier("taskKafkaConsumerListenerFactory") ConsumerFactory<String, TaskDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TaskDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            logger.error("RetryListeners massage {}, offset = {}, deliveryAttempt = {} ", ex.getMessage(), record.offset(), deliveryAttempt);
        });
        return handler;
    }

    @Bean
    public ProducerFactory<String, TaskDto> taskKafkaProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TaskDto> kafkaTemplate(ProducerFactory<String, TaskDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "t1.kafka.producer.enabled", havingValue = "true", matchIfMissing = true)
    public TaskKafkaProducer taskKafkaProducer(@Qualifier("kafkaTemplate") KafkaTemplate<String, TaskDto> kafkaTemplate) {
        kafkaTemplate.setDefaultTopic(taskTopic);
        return new TaskKafkaProducer(kafkaTemplate);
    }
}
