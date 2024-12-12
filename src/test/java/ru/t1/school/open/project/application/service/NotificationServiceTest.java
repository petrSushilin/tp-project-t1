package ru.t1.school.open.project.application.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.domain.enums.TaskStatus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {
    private static ByteArrayOutputStream out;
    private static PrintStream original;

    private static ListAppender<ILoggingEvent> notificationListAppender;

    @BeforeAll
    static void setUp() {
        // Слушаем Logger работающий с сервисом уведомлений
        Logger notificationLogger = (Logger) LoggerFactory.getLogger(NotificationService.class);
        notificationListAppender = new ListAppender<>();
        notificationListAppender.start();
        notificationLogger.addAppender(notificationListAppender);
    }

    @Test
    @DisplayName("Корректность добавления нового получателя по email")
    void registerRecipientTest() {
        NotificationService notificationService = new NotificationService();
        String exampleEmail = "test@example.com";
        notificationService.registerRecipient(exampleEmail);

        assertThat(notificationListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Recipient's email: " + exampleEmail + " has been registered"));
    }

    @ParameterizedTest
    @CsvSource({"test@example.com, null",
            "test@example.com, test2@example.com"})
    @DisplayName("Корректность отправки уведомления зарегистрированным получателям")
    void notificationTest(String email1, String email2) {
        NotificationService notificationService = new NotificationService();
        notificationService.registerRecipient(email1);
        if (email2 != null) {
            notificationService.registerRecipient(email2);
        }

        TaskDto task = new TaskDto(1L, "title", "description", TaskStatus.CREATED, 1L);
        notificationService.notification(List.of(task));

        assertThat(notificationListAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Recipient's email: " + email1 + " has been registered"));
        if (email2 != null) {
            assertThat(notificationListAppender.list)
                    .extracting(ILoggingEvent::getFormattedMessage)
                    .anyMatch(msg -> msg.contains("Recipient's email: " + email2 + " has been registered"));
        }
    }
}