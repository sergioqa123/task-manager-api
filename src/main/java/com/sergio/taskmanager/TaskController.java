package com.sergio.taskmanager;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/tasks")
class TaskController {

    TaskRepository taskRepository;

    private TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Task> findById(@PathVariable Long requestedId) {
        Optional<Task> task = taskRepository.findById(requestedId);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createTask(@RequestBody Task newTaskRequest, UriComponentsBuilder ucb) {
        Task savedTask = taskRepository.save(newTaskRequest);
        URI locationOfNewTask = ucb.path("/tasks/{id}").buildAndExpand(savedTask.id()).toUri();
        return ResponseEntity.created(locationOfNewTask).build();
    }
    
}
