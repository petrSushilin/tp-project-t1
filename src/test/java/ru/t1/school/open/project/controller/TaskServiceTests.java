package ru.t1.school.open.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.global.exception.RecordNotFoundException;
import ru.t1.school.open.project.global.exception.TaskValidationException;
import ru.t1.school.open.project.repo.TaskRepository;
import ru.t1.school.open.project.application.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskServiceTests {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        existingTask = new Task();
        existingTask.setTitle("Task#InitialTitle");
        existingTask.setDescription("Initial Description");
        existingTask.setUserId(1L);

        Task savedTask = taskService.create(existingTask);

        existingTask.setId(savedTask.getId());
    }

    @Test
    void testUpdateTask() {
        Task updatedTask = new Task();
        updatedTask.setId(existingTask.getId());
        updatedTask.setTitle("Task#NewTitle");
        updatedTask.setDescription("New Description");
        updatedTask.setUserId(2L);

        Task changedTask = taskService.change(String.valueOf(updatedTask.getId()), updatedTask);

        assertNotNull(changedTask);
        assertEquals("Task#NewTitle", changedTask.getTitle());
        assertTrue(changedTask.getDescription().contains("New Description") &&
                changedTask.getDescription().contains("CHANGED at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
        assertEquals(2L, changedTask.getUserId());

        Task taskFromDb = taskService.getById(String.valueOf(existingTask.getId()));
        assertEquals("Task#NewTitle", changedTask.getTitle());
        assertTrue(changedTask.getDescription().contains("New Description") &&
                changedTask.getDescription().contains("CHANGED at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
        assertEquals(2L, taskFromDb.getUserId());
    }

    @Test
    void testGetByIdOkay() {
        Task taskFromDb = taskService.getById(String.valueOf(existingTask.getId()));
        assertEquals("Task#InitialTitle", existingTask.getTitle());
        assertTrue(existingTask.getDescription().contains("Initial Description"));
        assertEquals(1L, taskFromDb.getUserId());
    }

    @Test
    void testGetByIdThrow() {
        String invalidTaskId = "12387419121";

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> taskService.getById(invalidTaskId)
        );

        assertTrue(exception.getMessage().contains("Record with id " + invalidTaskId + " not found"));
    }

    @Test
    void testInvalidCreationEmptyDescription() {
        Task newInvalidTask = new Task();
        newInvalidTask.setTitle("Task#OkayTitle");
        newInvalidTask.setDescription("");
        newInvalidTask.setUserId(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.create(newInvalidTask));
        assertTrue(exception.getMessage().contains("Description cannot be empty"));
    }

    @Test
    void testInvalidCreationIncorrectTitle() {
        Task newInvalidTask = new Task();
        newInvalidTask.setTitle("Tusk#IncorrectTitle");
        newInvalidTask.setDescription("Initial Description");
        newInvalidTask.setUserId(1L);

        TaskValidationException exception = assertThrows(TaskValidationException.class, () -> taskService.create(newInvalidTask));
        assertTrue(exception.getMessage().contains("Incorrect Task Title format."));
    }

    @Test
    void testInvalidCreationIncorrect() {
        Object invalidEntityType = new Object();
        TaskValidationException exception = assertThrows(TaskValidationException.class, () -> taskService.create(null));
        assertTrue(exception.getMessage().contains("Task is required"));
    }
}
