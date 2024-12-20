package com.example.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Async
    public void processTask(Long taskId) {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("Processing task with ID: " + taskId);
            Thread.sleep(5000); // Эмуляция долгой операции
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Task processing interrupted for ID: " + taskId);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Task with ID " + taskId + " processed in " + duration + " ms");
        }
    }

    @Async
    public void processTasksBulk(List<Long> taskIds) {
        System.out.println("Starting bulk task processing for IDs: " + taskIds);
        taskIds.forEach(taskId -> {
            try {
                Thread.sleep(1000); // Эмуляция обработки задачи (1 секунда на задачу)
                System.out.println("Processed task with ID: " + taskId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Processing interrupted for task ID: " + taskId);
            }
        });
        System.out.println("Completed bulk task processing.");
    }
}
