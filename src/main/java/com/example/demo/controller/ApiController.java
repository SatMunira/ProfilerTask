package com.example.demo.controller;
import com.example.demo.service.ExternalApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ExternalApiService externalApiService;

    public ApiController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/fetch")
    public String fetchData() {
        return externalApiService.fetchDataFromExternalApi();
    }

    @GetMapping("/fetch-delay")
    public String fetchWithDelay() {
        return externalApiService.fetchWithDelay();
    }

    @GetMapping("/fetch-error-handling")
    public String fetchDataWithErrorHandling() {
        return externalApiService.fetchDataWithErrorHandling();
    }
}
