package ru.t1.school.open.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.aspect.annotation.Changeable;
import ru.t1.school.open.project.entity.Task;
import ru.t1.school.open.project.repo.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(@NonNull Task task) {
        return taskRepository.save(task);
    }

    public Task getById(@NonNull String id) {
        return taskRepository.getReferenceById(Long.parseLong(id));
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Changeable
    public Task change(@NonNull String id, @NonNull Task task) {
        task.setId(Long.parseLong(id));
        return taskRepository.save(task);
    }

    public void remove(@NonNull String id) {
        taskRepository.deleteById(Long.parseLong(id));
    }
}
