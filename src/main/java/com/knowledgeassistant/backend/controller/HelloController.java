package com.knowledgeassistant.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String sayHello() {
        return "AI Knowledge Assistant backend is running!";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Backend is healthy and ready!";
    }
}