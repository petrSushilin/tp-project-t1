package ru.t1.school.open.project.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.t1.school.open.logger.annotation.Logging;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.kafka.TaskKafkaProducer;
import ru.t1.school.open.project.application.util.mapper.TaskMapper;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.repo.TaskRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskKafkaProducer kafkaProducer;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskKafkaProducer kafkaProducer) {
        this.taskRepository = taskRepository;
        this.kafkaProducer = kafkaProducer;
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

    @Logging
    public TaskDto getById(@NonNull String id) {
        return taskRepository
                .findById(Long.parseLong(id))
                .map(TaskMapper::toDto)
                .orElseThrow();
    }

    @Logging
    public List<TaskDto> getAll() {
        return taskRepository
                .findAll()
                .stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    @Logging
    public TaskDto change(@NonNull String id, TaskDto taskDto) {
        Task entity = TaskMapper.toEntity(taskDto);
        entity.setId(Long.parseLong(id));
        Task savedEntity = this.changeSaved(entity);
        return TaskMapper.toDto(savedEntity);
    }

    @Logging
    private Task changeSaved(Task updatedTask) {
        Task savedTask = taskRepository.findById(updatedTask.getId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if(!savedTask.getStatus().equals(updatedTask.getStatus())) {
            savedTask = taskRepository.save(updatedTask);
            kafkaProducer.send(TaskMapper.toDto(updatedTask));
        } else {
            savedTask = taskRepository.save(updatedTask);
        }
        return savedTask;
    }

    @Logging
    public void remove(@NonNull String id) {
        taskRepository.deleteById(Long.parseLong(id));
    }
}
