package ru.t1.school.open.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.school.open.project.entity.Task;
import ru.t1.school.open.project.service.TaskService;

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
    public ResponseEntity<?> create(@RequestBody Task task) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.create(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getById(id));
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> change(@PathVariable String id, @RequestBody Task task) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.change(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id) {
        taskService.remove(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Task with id " + id + " removed.");
    }
}
