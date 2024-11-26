package ru.t1.school.open.project.application.service;

import org.springframework.stereotype.Service;
import ru.t1.school.open.project.api.dto.TaskDto;

import java.util.List;
import java.util.Set;

@Service
public class NotificationService {
    private final Set<String> emailAddresses = Set.of("petrsushilin@mail.ru");

    void register(String email) {
//        emailAddresses.add(email);
    }

    public void notification(List<TaskDto> tasks) {
        tasks.forEach(task -> emailAddresses.forEach(email -> sendEmail(email, task)));
    }

    private void sendEmail(String email, TaskDto task) {
        System.out.println("Sending email to " + email + " about task: " + task.title() + " with status: " + task.status() + "...");
    }
}
