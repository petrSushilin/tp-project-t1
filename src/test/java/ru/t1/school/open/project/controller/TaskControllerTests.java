package ru.t1.school.open.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;
    /*
    restTemplate.exchange(
                "/api/v1/tasks",
                HttpMethod.POST,
                new HttpEntity<>(existingTask),
                Task.class
        );
        var response = restTemplate.exchange(
                "/api/v1/tasks/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedTask),
                Task.class,
                existingTask.getId()
        );

        var responseGetReturning = restTemplate.exchange(
                "/api/v1/tasks/{id}",
                HttpMethod.GET,
                null,
                Task.class,
                existingTask.getId()
        );
     */
}
