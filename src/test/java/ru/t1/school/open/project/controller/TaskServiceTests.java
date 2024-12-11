package ru.t1.school.open.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.api.dto.UserDto;
import ru.t1.school.open.project.application.service.UserService;
import ru.t1.school.open.project.application.util.mapper.TaskMapper;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.domain.enums.TaskStatus;
import ru.t1.school.open.project.domain.enums.UserRoles;
import ru.t1.school.open.project.repo.TaskRepository;
import ru.t1.school.open.project.application.service.TaskService;
import ru.t1.school.open.project.repo.UserRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class TaskServiceTests {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private TaskDto existingTask;
    private UserDto existingUser;
    private UserDto updatedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        taskRepository.deleteAll();

        existingUser = userService
                .create(new UserDto(0L, "user", "password", null));
        updatedUser = userService
                .create(new UserDto(0L, "user2", "password", null));

        existingTask = taskService
                .create(new TaskDto( 0L,"Task#InitialTitle", "Initial Description", null, existingUser.id()));
    }

    @Test
    void testUpdateTask() {
        Task updatedTask = new Task();
        updatedTask.setId(existingTask.id());
        updatedTask.setTitle("Task#NewTitle");
        updatedTask.setDescription("New Description");
        updatedTask.setStatus(existingTask.status());
        updatedTask.setUserId(updatedUser.id());

        TaskDto changedTask = taskService
                .change(String.valueOf(updatedTask.getId()), TaskMapper.toDto(updatedTask));

        assertNotNull(changedTask);
        assertEquals("Task#NewTitle", changedTask.title());
        assertEquals(updatedUser.id(), changedTask.userId());

        TaskDto taskFromDb = taskService.getById(String.valueOf(existingTask.id()));
        assertEquals("Task#NewTitle", changedTask.title());
        assertEquals(updatedUser.id(), taskFromDb.userId());
    }

    @Test
    void testGetByIdOkay() {
        TaskDto taskFromDb = taskService.getById(String.valueOf(existingTask.id()));
        assertEquals("Task#InitialTitle", existingTask.title());
        assertTrue(existingTask.description().contains("Initial Description"));
        assertEquals(existingUser.id(), taskFromDb.userId());
    }

    @Test
    void testGetByIdThrow() {
        String invalidTaskId = "12387419121";
        assertThrows(RuntimeException.class,() -> taskService.getById(invalidTaskId));
    }

    @Test
    void testCreation() {
        TaskDto newTask = taskService.create(new TaskDto( 0L,"Task#NewTitle", "New Description", null, updatedUser.id()));
        assertEquals("Task#NewTitle", newTask.title());
        assertEquals("New Description", newTask.description());
        assertEquals(TaskStatus.CREATED, newTask.status());
        assertEquals(updatedUser.id(), newTask.userId());
    }

    @Test
    void testKafkaEventChangeStatus() {
        Task currentTask = TaskMapper.toEntity(existingTask);
        currentTask.setStatus(TaskStatus.IN_PROGRESS);
        TaskDto changedTask = taskService.change(String.valueOf(existingTask.id()), TaskMapper.toDto(currentTask));
        assertEquals(TaskStatus.IN_PROGRESS, changedTask.status());
    }

    @Test
    void taskKafkaEventChangeDescription() {
        Task currentTask = TaskMapper.toEntity(existingTask);
        currentTask.setDescription("New Description");
        TaskDto changedTask = taskService.change(String.valueOf(existingTask.id()), TaskMapper.toDto(currentTask));
        assertEquals("New Description", changedTask.description());
    }
}
