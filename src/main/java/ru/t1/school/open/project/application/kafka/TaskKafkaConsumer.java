package ru.t1.school.open.project.application.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.service.NotificationService;

import java.util.List;

@Component
public class TaskKafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TaskKafkaConsumer.class);

    private final NotificationService notificationService;

    public TaskKafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${t1.kafka.topic.task}", containerFactory = "taskKafkaListenerContainerFactory")
    public void listener(@Payload List<TaskDto> massageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
        try {
            logger.info("Received message from topic: {}. Received messages: {}", topic, massageList);
            notificationService.notification(massageList);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("System error while processing message. Topic: {}, Key: {}, Message: {}", topic, key, massageList, e);
        }
    }
}
