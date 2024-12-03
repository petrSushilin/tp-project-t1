package ru.t1.school.open.project.application.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.school.open.project.api.dto.TaskDto;

import java.util.UUID;

public class TaskKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(TaskKafkaProducer.class);

    private final KafkaTemplate<String, TaskDto> kafkaTemplate;

    public TaskKafkaProducer(KafkaTemplate<String, TaskDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(TaskDto dto) {
        try {
            kafkaTemplate.sendDefault(UUID.randomUUID().toString(), dto).get();
            kafkaTemplate.flush();
        } catch (Exception e) {
            logger.error("Error sending message to Kafka", e);
        }
    }
}
