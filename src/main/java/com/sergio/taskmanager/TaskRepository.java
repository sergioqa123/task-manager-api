package com.sergio.taskmanager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface TaskRepository extends CrudRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {
    Task findByIdAndOwner(Long id, String owner);
    Page<Task> findByOwner(String owner, Pageable pageable);
}
