package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Поиск задач по статусу (completed)
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
}
