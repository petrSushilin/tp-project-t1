package ru.t1.school.open.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.mapper.TaskMapper;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.global.exception.RecordNotFoundException;
import ru.t1.school.open.project.repo.TaskRepository;
import ru.t1.school.open.project.application.service.TaskService;

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

    private TaskDto existingTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        existingTask = taskService
                .create(new TaskDto( 0L,"Task#InitialTitle", "Initial Description", 1L));
    }

    @Test
    void testUpdateTask() {
        Task updatedTask = new Task();
        updatedTask.setId(existingTask.id());
        updatedTask.setTitle("Task#NewTitle");
        updatedTask.setDescription("New Description");
        updatedTask.setUserId(2L);

        TaskDto changedTask = taskService
                .change(String.valueOf(updatedTask.getId()), TaskMapper.toDto(updatedTask));

        assertNotNull(changedTask);
        assertEquals("Task#NewTitle", changedTask.title());
        System.out.println(changedTask);
        assertEquals(2L, changedTask.userId());

        TaskDto taskFromDb = taskService.getById(String.valueOf(existingTask.id()));
        assertEquals("Task#NewTitle", changedTask.title());
        assertEquals(2L, taskFromDb.userId());
    }

    @Test
    void testGetByIdOkay() {
        TaskDto taskFromDb = taskService.getById(String.valueOf(existingTask.id()));
        assertEquals("Task#InitialTitle", existingTask.title());
        assertTrue(existingTask.description().contains("Initial Description"));
        assertEquals(1L, taskFromDb.userId());
    }

    @Test
    void testGetByIdThrow() {
        String invalidTaskId = "12387419121";
        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> taskService.getById(invalidTaskId)
        );
        assertTrue(exception.getMessage().contains("Record with id " + invalidTaskId + " not found"));
    }
}
