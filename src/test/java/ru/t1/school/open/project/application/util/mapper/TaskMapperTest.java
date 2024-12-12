package ru.t1.school.open.project.application.util.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.domain.enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskMapperTest {

    @Test
    void toDto() {
        Long testTaskId = 1L;
        String testTaskTittle = "Test task";
        String testTaskDescription = "Test task description";
        TaskStatus testTaskStatus = TaskStatus.IN_PROGRESS;
        Long testUserId = 2L;

        Task task = new Task();
        task.setId(testTaskId);
        task.setTitle(testTaskTittle);
        task.setDescription(testTaskDescription);
        task.setStatus(testTaskStatus);
        task.setUserId(testUserId);

        TaskDto taskDto = TaskMapper.toDto(task);

        assertEquals(testTaskId, taskDto.id());
        assertEquals(testTaskTittle, taskDto.title());
        assertEquals(testTaskDescription, taskDto.description());
        assertEquals(testTaskStatus, taskDto.status());
        assertEquals(testUserId, taskDto.userId());
    }

    @Test
    void toEntity() {
        Long testTaskId = 1L;
        String testTaskTittle = "Test task";
        String testTaskDescription = "Test task description";
        TaskStatus testTaskStatus = TaskStatus.IN_PROGRESS;
        Long testUserId = 2L;

        TaskDto taskDto = new TaskDto(testTaskId, testTaskTittle, testTaskDescription, testTaskStatus, testUserId);
        Task task = TaskMapper.toEntity(taskDto);

        assertEquals(testTaskId, task.getId());
        assertEquals(testTaskTittle, task.getTitle());
        assertEquals(testTaskDescription, task.getDescription());
        assertEquals(testTaskStatus, task.getStatus());
        assertEquals(testUserId, task.getUserId());
    }
}