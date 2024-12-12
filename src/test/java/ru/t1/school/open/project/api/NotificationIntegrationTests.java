package ru.t1.school.open.project.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.school.open.project.application.service.NotificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("ИТ. Добавление получателя уведомления")
    void createTaskTest() throws Exception {
        // Слушаем Logger работающий с сервисом уведомлений
        Logger notificationLogger = (Logger) LoggerFactory.getLogger(NotificationService.class);
        ListAppender<ILoggingEvent> notificationListAppender = new ListAppender<>();
        notificationListAppender.start();
        notificationLogger.addAppender(notificationListAppender);

        String exampleEmail = "example@example.com";

        String response = mockMvc.perform(post("/api/v1/notifications/registration")
                        .contentType("application/json")
                        .param("email", exampleEmail))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Проверяем выполняется ли логирование успешной обработки запроса
        assertThat(notificationListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Recipient's email: " + exampleEmail + " has been registered"));

        // Проверяем корректность обработки данных при создании
        assertThat(response)
                .isEqualTo("true");
    }


}
