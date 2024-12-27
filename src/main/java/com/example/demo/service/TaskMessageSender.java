package com.example.demo.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskMessageSender {

    private final RabbitTemplate rabbitTemplate;

    public TaskMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTaskNotification(String taskId, String action) {
        String message = "Task " + taskId + " has been " + action;
        rabbitTemplate.convertAndSend("task-exchange", "task.notify", message);
        System.out.println("Message sent: " + message);
    }
}
