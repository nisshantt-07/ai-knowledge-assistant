package com.knowledgeassistant.backend.controller;

import com.knowledgeassistant.backend.entity.ChatHistory;
import com.knowledgeassistant.backend.repository.ChatHistoryRepository;
import com.knowledgeassistant.backend.service.PythonServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private PythonServiceClient pythonServiceClient;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

            Map<String, Object> response = pythonServiceClient.askQuestion(question);
            String answer = (String) response.get("answer");

            // Database me save karo
            ChatHistory chatEntry = new ChatHistory(currentUserEmail, question, answer, LocalDateTime.now());
            chatHistoryRepository.save(chatEntry);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Chat failed: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ChatHistory> history = chatHistoryRepository.findByUserEmailOrderByTimestampAsc(currentUserEmail);
        return ResponseEntity.ok(history);
    }
}