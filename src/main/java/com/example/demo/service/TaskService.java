package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Async
    public void processTask(Long taskId) {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("Processing task with ID: " + taskId);
            Thread.sleep(5000);
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

    @Async
    public void processLargeDataSet() {
        System.out.println("Starting large dataset processing...");

        List<Integer> data = IntStream.range(0, 1_000_000)
                .map(i -> ThreadLocalRandom.current().nextInt(1, 10_000))
                .boxed()
                .collect(Collectors.toList());

        long startTime = System.currentTimeMillis();
        data.sort(Integer::compareTo);
        System.out.println("Sorting completed in " + (System.currentTimeMillis() - startTime) + " ms");

        int max = data.stream().max(Integer::compareTo).orElseThrow();
        int min = data.stream().min(Integer::compareTo).orElseThrow();
        double avg = data.stream().mapToInt(Integer::intValue).average().orElseThrow();

        System.out.println("Statistics: Max = " + max + ", Min = " + min + ", Avg = " + avg);
        System.out.println("Large dataset processing completed.");
    }

    @Async
    public void processLargeFile(String filePath) {
        System.out.println("Starting file processing...");

        try {
            Path path = Paths.get(filePath);
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (int i = 0; i < 1_000_000; i++) {
                    writer.write("Task " + i + ",Description " + i + ",false\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("File generation completed: " + filePath);

            long lineCount = Files.lines(path).count();
            System.out.println("File contains " + lineCount + " lines.");

        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }

        System.out.println("File processing completed.");
    }

    public void generateLargeDataSet(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setDescription("Description for task " + i);
            task.setCompleted(i % 2 == 0); // Чередование completed = true/false
            tasks.add(task);
        }
        taskRepository.saveAll(tasks);
        System.out.println(count + " tasks created successfully.");
    }

    @Transactional
    public void updateTaskStatusConcurrently(Long taskId, boolean newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        System.out.println("Task " + taskId + " current status: " + task.isCompleted());

        task.setCompleted(newStatus);
        taskRepository.save(task);

        System.out.println("Task " + taskId + " updated to status: " + newStatus);
    }

}
