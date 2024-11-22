package ru.t1.school.open.project.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.aspect.annotation.Existing;
import ru.t1.school.open.project.application.aspect.annotation.Logging;
import ru.t1.school.open.project.application.mapper.TaskMapper;
import ru.t1.school.open.project.repo.TaskRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Logging
    public TaskDto create(TaskDto taskDto) {
        return Stream.of(taskDto)
                .map(TaskMapper::toEntity)
                .map(taskRepository::save)
                .map(TaskMapper::toDto)
                .findFirst()
                .orElseThrow();
    }

    @Existing
    public TaskDto getById(@NonNull String id) {
        return taskRepository
                .findById(Long.parseLong(id))
                .map(TaskMapper::toDto)
                .orElseThrow();
    }

    public List<TaskDto> getAll() {
        return taskRepository
                .findAll()
                .stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    @Existing
    public TaskDto change(@NonNull String id, TaskDto taskDto) {
        return Stream.of(taskDto)
                .map(TaskMapper::toEntity)
                .peek(task -> task.setId(Long.parseLong(id)))
                .map(taskRepository::save)
                .map(TaskMapper::toDto)
                .findFirst()
                .orElseThrow();
    }

    @Existing
    public void remove(@NonNull String id) {
        taskRepository.deleteById(Long.parseLong(id));
    }
}
