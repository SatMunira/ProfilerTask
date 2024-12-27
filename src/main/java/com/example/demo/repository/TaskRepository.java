package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByCompleted(boolean completed, Pageable pageable);

    List<Task> findByTitleContainingOrDescriptionContaining(String title, String description);

    @Query("SELECT t.completed, COUNT(t) FROM Task t GROUP BY t.completed")
    List<Object[]> countTasksByCompletionStatus();

}
