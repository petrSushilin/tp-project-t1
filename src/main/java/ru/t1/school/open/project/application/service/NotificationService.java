package ru.t1.school.open.project.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.api.dto.TaskDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final Set<String> emailAddresses = new HashSet<>();

    public boolean registerRecipient(String email) {
        emailAddresses.add(email);
        logger.info("Recipient's email: {} has been registered", email);
        return true;
    }

    public void notification(List<TaskDto> tasks) {
        tasks.forEach(task -> emailAddresses.forEach(email -> sendEmail(email, task)));
    }

    private void sendEmail(String email, TaskDto task) {
        logger.info("Sending email to {} about task: {} with set status: {}", email, task.title(), task.status());
    }
}
