package ru.t1.school.open.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import ru.t1.school.open.project.entity.Task;
import ru.t1.school.open.project.repo.TaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TaskRepository taskRepository;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        existingTask = new Task();
        existingTask.setTitle("Initial Title");
        existingTask.setDescription("Initial Description");
        existingTask.setUserId(1L);

        Task savedTask = taskRepository.save(existingTask);

        System.out.println(savedTask.toString());
        existingTask.setId(savedTask.getId());
    }

    @Test
    void testUpdateTask() {
        Task updatedTask = new Task();
        updatedTask.setId(existingTask.getId());
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setUserId(2L);

        var response = restTemplate.exchange(
                "/api/v1/tasks/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedTask),
                Task.class,
                existingTask.getId()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Task responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("New Title", responseBody.getTitle());
        assertTrue(responseBody.getDescription().contains("New Description") &&
                responseBody.getDescription().contains("CHANGED at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
        assertEquals(2L, responseBody.getUserId());
        System.out.println(responseBody.toString());

        Task taskFromDb = taskRepository.findById(existingTask.getId()).orElseThrow();
        assertEquals("New Title", responseBody.getTitle());
        assertTrue(responseBody.getDescription().contains("New Description") &&
                responseBody.getDescription().contains("CHANGED at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
        assertEquals(2L, taskFromDb.getUserId());
    }
}
