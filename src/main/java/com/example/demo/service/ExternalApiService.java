package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {
    private final WebClient webClient;
    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String fetchDataFromExternalApi() {
        return webClient.get()
                .uri("/posts/1")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String fetchWithDelay() {
        try {
            return webClient.get()
                    .uri("/delay/5")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(java.time.Duration.ofSeconds(3))
                    .block();
        } catch (Exception e) {
            return "Request timed out";
        }
    }

    public String fetchDataWithErrorHandling() {
        return webClient.get()
                .uri("/invalid-endpoint")
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    System.out.println("Client error!");
                    return Mono.error(new RuntimeException("4xx error occurred"));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    System.out.println("Server error!");
                    return Mono.error(new RuntimeException("5xx error occurred"));
                })
                .bodyToMono(String.class)
                .block();
    }

}
