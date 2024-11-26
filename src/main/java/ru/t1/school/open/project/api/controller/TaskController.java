package ru.t1.school.open.project.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.application.kafka.TaskKafkaConsumer;
import ru.t1.school.open.project.application.kafka.TaskKafkaProducer;
import ru.t1.school.open.project.domain.entity.Task;
import ru.t1.school.open.project.application.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@RequestBody TaskDto task) {
        return taskService.create(task);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto getById(@PathVariable String id) {
        return taskService.getById(id);
    }

    @GetMapping()
    public List<TaskDto> getAll() {
        return taskService.getAll();
    }

    @PutMapping("/{id}")
    public TaskDto change(@PathVariable String id, @RequestBody TaskDto task) {
        return taskService.change(id, task);
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable String id) {
        taskService.remove(id);
        return "Task with id " + id + " removed.";
    }
}
