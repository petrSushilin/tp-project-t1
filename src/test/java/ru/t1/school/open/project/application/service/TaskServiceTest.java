package ru.t1.school.open.project.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.kafka.TaskKafkaProducer;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.domain.enums.TaskStatus;
import ru.t1.school.open.project.repo.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    private Task exampleTask;
    private TaskDto exampleTaskDto;

    @BeforeEach
    void setUp() {
        exampleTask = new Task(1L, "Task#InitialTitle", "Initial Description", TaskStatus.CREATED, 1L);
        exampleTaskDto = new TaskDto(1L, "Task#InitialTitle", "Initial Description", TaskStatus.CREATED, 1L);
    }

    @Test
    @DisplayName("МТ. Создание задачи")
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(exampleTask);

        TaskDto result = taskService.create(exampleTaskDto);

        assertNotNull(result);
        assertEquals(exampleTaskDto.id(), result.id());
        assertEquals(exampleTaskDto.title(), result.title());
        assertEquals(exampleTaskDto.description(), result.description());
        assertEquals(exampleTaskDto.status(), result.status());
        assertEquals(exampleTaskDto.userId(), result.userId());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("МТ. Получение задачи по идентификатору")
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(exampleTask));

        TaskDto result = taskService.getById(String.valueOf(exampleTaskDto.id()));

        assertNotNull(result);
        assertEquals(exampleTaskDto.id(), result.id());
        assertEquals(exampleTaskDto.title(), result.title());
        assertEquals(exampleTaskDto.description(), result.description());
        assertEquals(exampleTaskDto.status(), result.status());
        assertEquals(exampleTaskDto.userId(), result.userId());

        verify(taskRepository).findById(1L);
    }

    @Test
    @DisplayName("МТ. Получение всех задач")
    void testGetAllTasks() {
        Task anotherTask = new Task(25L, "Task#Another", "Another Description", TaskStatus.IN_PROGRESS, 100L);
        when(taskRepository.findAll()).thenReturn(List.of(exampleTask, anotherTask));

        List<TaskDto> result = taskService.getAll();

        assertNotNull(result);
        TaskDto resultTask = result.get(0);
        assertEquals(exampleTaskDto.id(), resultTask.id());
        assertEquals(exampleTaskDto.title(), resultTask.title());
        assertEquals(exampleTaskDto.description(), resultTask.description());
        assertEquals(exampleTaskDto.status(), resultTask.status());
        assertEquals(exampleTaskDto.userId(), resultTask.userId());

        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("МТ. Изменение задачи")
    void testChangeTask() {
        TaskDto incomingTaskDto = new TaskDto(25L, "Task#Changed", "Changed Description", TaskStatus.IN_PROGRESS, 100L);
        Task taskInDb = new Task(25L, "Task#Changed", "Changed Description", TaskStatus.IN_PROGRESS, 100L);

        when(taskRepository.findById(incomingTaskDto.id())).thenReturn(Optional.of(taskInDb));
        when(taskRepository.save(any(Task.class))).thenReturn(taskInDb);

        TaskDto result = taskService.change(String.valueOf(incomingTaskDto.id()), incomingTaskDto);

        assertNotNull(result);
        assertEquals(taskInDb.getId(), result.id());
        assertEquals(taskInDb.getTitle(), result.title());
        assertEquals(taskInDb.getDescription(), result.description());
        assertEquals(taskInDb.getStatus(), result.status());
        assertEquals(taskInDb.getUserId(), result.userId());

        verify(taskRepository).findById(taskInDb.getId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Обработка исключения при изменении задачи")
    void testChangeTaskThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.change(String.valueOf(exampleTaskDto.id()), exampleTaskDto));

        verify(taskRepository).findById(exampleTaskDto.id());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("МТ. Удаление задачи")
    void testRemoveTask() {
        boolean result = taskService.remove(String.valueOf(exampleTaskDto.id()));

        assertTrue(result);

        verify(taskRepository).deleteById(exampleTaskDto.id());
    }
}