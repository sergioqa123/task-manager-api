package com.sergio.taskmanager;

import java.net.URI;
import java.security.Principal;
import java.util.List;

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
    private ResponseEntity<Task> findById(@PathVariable Long requestedId, Principal principal) {
        Task task = findTask(requestedId, principal);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createTask(@RequestBody Task newTaskRequest, UriComponentsBuilder ucb, Principal principal) {
        Task taskWithOwner = new Task(null, newTaskRequest.title(), newTaskRequest.description(), newTaskRequest.completed(), principal.getName());
        Task savedTask = taskRepository.save(taskWithOwner);
        URI locationOfNewTask = ucb.path("/tasks/{id}").buildAndExpand(savedTask.id()).toUri();
        return ResponseEntity.created(locationOfNewTask).build();
    }

    @GetMapping
    private ResponseEntity<List<Task>> findAll(Pageable pageable, Principal principal) {
        Page<Task> page = taskRepository.findByOwner(
            principal.getName(),
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
            )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTask(@PathVariable Long requestedId, @RequestBody Task taskUpdate, Principal principal) {
        Task task = findTask(requestedId, principal);
        if (task != null) {
            Task updatedTask = new Task(task.id(), taskUpdate.title(), taskUpdate.description(), taskUpdate.completed(), principal.getName());
            taskRepository.save(updatedTask);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Task findTask(Long requestedId, Principal principal) {
        return taskRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteTask(@PathVariable Long requestedId, Principal principal) {
        if (taskRepository.existsByIdAndOwner(requestedId, principal.getName())) {
            taskRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
