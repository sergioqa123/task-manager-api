package com.sergio.taskmanager;

import java.net.URI;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    private ResponseEntity<List<Task>> findAll(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
            )
        );
        return ResponseEntity.ok(page.getContent());
    }
    
}
