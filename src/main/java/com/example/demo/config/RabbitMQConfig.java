package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange("task-exchange");
    }

    @Bean
    public Queue taskQueue() {
        return new Queue("task-queue");
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange).with("task.#");
    }
}
