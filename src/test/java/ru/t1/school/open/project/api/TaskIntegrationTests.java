package ru.t1.school.open.project.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.school.open.logger.LoggingAspect;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.environment.PostgresNKafkaTestContainers;
import ru.t1.school.open.project.application.kafka.TaskKafkaConsumer;
import ru.t1.school.open.project.application.kafka.TaskKafkaProducer;
import ru.t1.school.open.project.application.service.NotificationService;
import ru.t1.school.open.project.application.util.mapper.TaskMapper;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.domain.enums.TaskStatus;
import ru.t1.school.open.project.repo.TaskRepository;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskIntegrationTests extends PostgresNKafkaTestContainers {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TaskRepository taskRepository;

    private ListAppender<ILoggingEvent> aspectLoggingListAppender;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TaskDto existingTask;
    private final Long existingUserId = 1L;
    private final Long updatedUserId = 2L;

    @BeforeEach
    void setUp() {
        // Слушаем Logger работающий с аспектом
        Logger aspectLogger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
        aspectLoggingListAppender = new ListAppender<>();
        aspectLoggingListAppender.start();
        aspectLogger.addAppender(aspectLoggingListAppender);

        taskRepository.deleteAll();
        // Добавляем базовую задачу
        Task initialTask = taskRepository.save(new Task(0L, "Task#InitialTitle", "Initial Description", null, existingUserId));
        existingTask = TaskMapper.toDto(initialTask);
    }

    @Test
    @DisplayName("ИТ. Создание записи при обращении к серверу")
    void createTaskTest() throws Exception {
        String response = mockMvc.perform(post("/api/v1/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new TaskDto( 0L,"Task#NewTitle", "New Description", null, existingUserId))
                        ))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDto responseTask = objectMapper.readValue(response, TaskDto.class);

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.create(..)"))
                .anyMatch(msg -> msg.contains("Method TaskDto ru.t1.school.open.project.application.service.TaskService.create(TaskDto) executed successfully."))
                .anyMatch(msg -> msg.contains("Method TaskService.create(..) executed"))
                .anyMatch(msg -> msg.contains("Method executed in"));

        // Проверяем корректность обработки данных при создании
        assertThat(responseTask.title())
                .isEqualTo("Task#NewTitle");
        assertThat(responseTask.description())
                .isEqualTo("New Description");
        assertThat(responseTask.status())
                .isEqualTo(TaskStatus.CREATED);
        assertThat(responseTask.userId())
                .isEqualTo(existingUserId);
    }

    @Test
    @DisplayName("ИТ. Получение записи по идентификатору с корректными данными")
    void testTaskGetByIdOkay() throws Exception {
        String response = mockMvc.perform(get("/api/v1/tasks/{id}", existingTask.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDto responseTask = objectMapper.readValue(response, TaskDto.class);

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.getById(..)"))
                .anyMatch(msg -> msg.contains("Method TaskDto ru.t1.school.open.project.application.service.TaskService.getById(String) executed successfully."))
                .anyMatch(msg -> msg.contains("Method TaskService.getById(..) executed"))
                .anyMatch(msg -> msg.contains("Method executed in"));

        // Проверяем корректность обработки данных при создании
        assertThat(responseTask.title())
                .isEqualTo("Task#InitialTitle");
        assertThat(responseTask.description())
                .isEqualTo("Initial Description");
        assertThat(responseTask.status())
                .isEqualTo(TaskStatus.CREATED);
        assertThat(responseTask.userId())
                .isEqualTo(existingUserId);
    }

    @Test
    @DisplayName("ИТ. Получение записи по идентификатору с некорректными данными")
    void testTaskGetByIdThrow() throws Exception {
        String invalidTaskId = "12387419121";
        ServletException servletException = assertThrows(ServletException.class, () -> mockMvc.perform(get("/api/v1/tasks/{id}", invalidTaskId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString());

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.getById(..)"))
                .anyMatch(msg -> msg.contains("Method TaskDto ru.t1.school.open.project.application.service.TaskService.getById(String) threw an exception"))
                .anyMatch(msg -> msg.contains("Method TaskService.getById(..) executed"));

        assertThat(servletException.getMessage())
                .contains("Runtime exception @Around aspect Logging");
    }

    @Test
    @DisplayName("ИТ. Получение всех записей")
    void testTaskGetAll() throws Exception {
        long anotherUserId = 120L;
        Task anotherTask = taskRepository.save(new Task(0L, "Task#NewTitle", "New Description", null, anotherUserId));
        TaskDto anotherTaskDto = TaskMapper.toDto(anotherTask);

        String response = mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TaskDto> tasks = objectMapper.readValue(response, new TypeReference<ArrayList<TaskDto>>() {});

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.getAll()"))
                .anyMatch(msg -> msg.contains("Method List ru.t1.school.open.project.application.service.TaskService.getAll() executed successfully."))
                .anyMatch(msg -> msg.contains("Method TaskService.getAll() executed"))
                .anyMatch(msg -> msg.contains("Method executed in"));

        // Проверяем корректность обработки данных при создании
        assertThat(tasks.size())
                .isGreaterThanOrEqualTo(2);
        TaskDto firstDto = tasks.get(0);
        TaskDto secondDto = tasks.get(1);
        assertThat(firstDto.id())
                .isNotEqualTo(secondDto.id());
        assertThat(firstDto.title())
                .isNotEqualTo(secondDto.title());
        assertThat(firstDto.description())
                .isNotEqualTo(secondDto.description());
        assertThat(firstDto.userId())
                .isNotEqualTo(secondDto.userId());
    }

    @Test
    @DisplayName("ИТ. Изменение статуса записи, отправка публикации в топик Kafka, и его последующее чтение")
    void testChangeTaskWithKafkaEvent() throws Exception {
        // Слушаем Logger работающий с продюсером кафки
        Logger kafkaProducerLogger = (Logger) LoggerFactory.getLogger(TaskKafkaProducer.class);
        ListAppender<ILoggingEvent> kafkaProducerListAppender = new ListAppender<>();
        kafkaProducerListAppender.start();
        kafkaProducerLogger.addAppender(kafkaProducerListAppender);

        // Слушаем Logger работающий с консьюмером кафки
        Logger kafkaConsumerLogger = (Logger) LoggerFactory.getLogger(TaskKafkaConsumer.class);
        ListAppender<ILoggingEvent> kafkaConsumerListAppender = new ListAppender<>();
        kafkaConsumerListAppender.start();
        kafkaConsumerLogger.addAppender(kafkaConsumerListAppender);

        // Слушаем Logger работающий с сервисом уведомлений
        Logger notificationLogger = (Logger) LoggerFactory.getLogger(NotificationService.class);
        ListAppender<ILoggingEvent> notificationListAppender = new ListAppender<>();
        notificationListAppender.start();
        notificationLogger.addAppender(notificationListAppender);
        // Добавляем почту для уведомления
        String exampleEmail = "test@example.com";
        notificationService.registerRecipient(exampleEmail);

        Task currentTask = TaskMapper.toEntity(existingTask);
        TaskStatus newTaskStatus = TaskStatus.IN_PROGRESS;
        currentTask.setStatus(newTaskStatus);
        String response = mockMvc.perform(put("/api/v1/tasks/{id}", existingTask.id())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(currentTask)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        sleep(100);

        TaskDto responseTask = objectMapper.readValue(response, TaskDto.class);

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.change(..)"))
                .anyMatch(msg -> msg.contains("Method TaskDto ru.t1.school.open.project.application.service.TaskService.change(String,TaskDto) executed successfully."))
                .anyMatch(msg -> msg.contains("Method TaskService.change(..) executed"))
                .anyMatch(msg -> msg.contains("Method executed in"));
        assertThat(kafkaProducerListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Published event to topic"));
        assertThat(kafkaConsumerListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Received message from topic"));
        assertThat(notificationListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Sending email to " + exampleEmail + " about task: " + currentTask.getTitle() + " with set status: " + currentTask.getStatus()));

        // Проверяем корректность измененного значения
        assertThat(responseTask.status())
                .isEqualTo(newTaskStatus);
    }

    @Test
    @DisplayName("ИТ. Изменение описания и идентификатора пользователя в записи, невыполнение работы Kafka")
    void taskChangeTaskWithoutKafkaEvent() throws Exception {
        Task currentTask = TaskMapper.toEntity(existingTask);
        String newDescription = "New Description";
        long newUserId = 2L;
        currentTask.setDescription(newDescription);
        currentTask.setUserId(newUserId);

        // Слушаем Logger работающий с продюсером кафки
        Logger kafkaProducerLogger = (Logger) LoggerFactory.getLogger(TaskKafkaProducer.class);
        ListAppender<ILoggingEvent> kafkaProducerListAppender = new ListAppender<>();
        kafkaProducerListAppender.start();
        kafkaProducerLogger.addAppender(kafkaProducerListAppender);

        // Слушаем Logger работающий с консьюмером кафки
        Logger kafkaConsumerLogger = (Logger) LoggerFactory.getLogger(TaskKafkaConsumer.class);
        ListAppender<ILoggingEvent> kafkaConsumerListAppender = new ListAppender<>();
        kafkaConsumerListAppender.start();
        kafkaConsumerLogger.addAppender(kafkaConsumerListAppender);

        String response = mockMvc.perform(put("/api/v1/tasks/{id}", existingTask.id())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(currentTask)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        sleep(500);

        TaskDto responseTask = objectMapper.readValue(response, TaskDto.class);

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.change(..)"))
                .anyMatch(msg -> msg.contains("Method TaskDto ru.t1.school.open.project.application.service.TaskService.change(String,TaskDto) executed successfully."))
                .anyMatch(msg -> msg.contains("Method TaskService.change(..) executed"))
                .anyMatch(msg -> msg.contains("Method executed in"));

        assertThat(kafkaProducerListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(msg -> msg.contains("Published event to topic"));
        assertThat(kafkaConsumerListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(msg -> msg.contains("Received message from topic"));

        // Проверяем корректность измененного значения
        assertThat(responseTask.description())
                .isEqualTo(newDescription);
        assertThat(responseTask.userId())
                .isEqualTo(newUserId);
    }

    @Test
    @DisplayName("ИТ. Удаление задачи")
    void removeTaskTest() throws Exception {
        String response = mockMvc.perform(delete("/api/v1/tasks/{id}", existingTask.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(aspectLoggingListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Executing method TaskService.remove(..)"))
                .anyMatch(msg -> msg.contains("Method boolean ru.t1.school.open.project.application.service.TaskService.remove(String) executed successfully."))
                .anyMatch(msg -> msg.contains("Method TaskService.remove(..) executed"))
                .anyMatch(msg -> msg.contains("Method executed in"));

        assertThat(response)
                .isEqualTo("true");
    }
}
