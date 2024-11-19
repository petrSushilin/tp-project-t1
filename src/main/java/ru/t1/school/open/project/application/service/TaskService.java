package ru.t1.school.open.project.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.application.aspect.annotation.Changeable;
import ru.t1.school.open.project.application.aspect.annotation.Existing;
import ru.t1.school.open.project.application.aspect.annotation.Logging;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.repo.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Logging
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Existing
    public Task getById(@NonNull String id) {
        return taskRepository.findById(Long.parseLong(id)).orElseThrow();
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Existing
    @Changeable
    public Task change(@NonNull String id, Task task) {
        task.setId(Long.parseLong(id));
        return taskRepository.save(task);
    }

    @Existing
    public void remove(@NonNull String id) {
        taskRepository.deleteById(Long.parseLong(id));
    }
}
