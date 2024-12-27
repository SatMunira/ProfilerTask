package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskMessageSender;
import org.springframework.http.ResponseEntity;
import com.example.demo.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final TaskMessageSender messageSender;

    public TaskController(TaskRepository taskRepository, TaskService taskService, TaskMessageSender messageSender) {
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.messageSender = messageSender;
    }


    // Получение задач с фильтрацией, сортировкой и пагинацией
    @GetMapping
    public Page<Task> getTasks(
            @RequestParam(required = false) Boolean completed,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        if (completed != null) {
            return taskRepository.findByCompleted(completed, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    // Асинхронная обработка задачи
    @PostMapping("/{id}/process")
    public String processTask(@PathVariable Long id) {
        taskService.processTask(id);
        return "Task with ID " + id + " is being processed asynchronously.";
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setCompleted(taskDetails.isCompleted());
                    taskRepository.save(task);
                    return ResponseEntity.ok(task);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk")
    public List<Task> createTasksBulk(@RequestBody List<Task> tasks) {
        return taskRepository.saveAll(tasks);
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteTasksBulk(@RequestBody List<Long> ids) {
        taskRepository.deleteAllById(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk/process")
    public String processTasksBulk(@RequestBody List<Long> taskIds) {
        taskService.processTasksBulk(taskIds);
        return "Tasks are being processed in the background: " + taskIds;
    }

    @PostMapping("/process/large-dataset")
    public String processLargeDataSet() {
        taskService.processLargeDataSet();
        return "Large dataset processing started in the background.";
    }

    @PostMapping("/process/file")
    public String processLargeFile(@RequestParam String filePath) {
        taskService.processLargeFile(filePath);
        return "File processing started in the background for: " + filePath;
    }

    @PostMapping("/{id}/status")
    public String updateTaskStatus(@PathVariable Long id, @RequestParam boolean status) {
        taskService.updateTaskStatusConcurrently(id, status);
        messageSender.sendTaskNotification(String.valueOf(id), "updated");
        return "Task " + id + " status update to " + status + " requested.";
    }

    @GetMapping("/search")
    public List<Task> searchTasks(@RequestParam String query) {
        return taskRepository.findByTitleContainingOrDescriptionContaining(query, query);
    }

    @PostMapping("/generate")
    public String generateTasks(@RequestParam int count) {
        taskService.generateLargeDataSet(count);
        return count + " tasks are being generated.";
    }

    @GetMapping("/stats/completed")
    public List<Object[]> getTaskCompletionStats() {
        return taskRepository.countTasksByCompletionStatus();
    }


}
