package ru.t1.school.open.project.application.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.school.open.project.api.dto.TaskDto;

public class TaskKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(TaskKafkaProducer.class);

    private final KafkaTemplate<String, TaskDto> kafkaTemplate;

    public TaskKafkaProducer(KafkaTemplate<String, TaskDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(TaskDto message) {
        kafkaTemplate.sendDefault(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("Published event to topic {}: value = {}", kafkaTemplate.getDefaultTopic(), message.toString());
                    } else {
                        logger.error("Error sending message to Kafka", ex);
                        throw new RuntimeException("Caught an exception", ex);
                    }
                });
    }
}
