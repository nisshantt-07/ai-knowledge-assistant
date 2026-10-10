package com.knowledgeassistant.backend.controller;

import com.knowledgeassistant.backend.entity.Document;
import com.knowledgeassistant.backend.service.DocumentService;
import com.knowledgeassistant.backend.service.PythonServiceClient;
import com.knowledgeassistant.backend.service.TextExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TextExtractionService textExtractionService;

    @Autowired
    private PythonServiceClient pythonServiceClient;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Document savedDocument = documentService.uploadDocument(file, currentUserEmail);
            return ResponseEntity.ok(savedDocument);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/my-documents")
    public ResponseEntity<?> getMyDocuments() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Document> documents = documentService.getDocumentsByUser(currentUserEmail);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}/extract-text")
    public ResponseEntity<?> extractText(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            String text = textExtractionService.extractTextFromPdf(document.getFilePath());
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Extraction failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/chunk-text")
    public ResponseEntity<?> chunkDocumentText(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            String text = textExtractionService.extractTextFromPdf(document.getFilePath());
            Map<String, Object> chunksResponse = pythonServiceClient.getChunks(text);
            return ResponseEntity.ok(chunksResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Chunking failed: " + e.getMessage());
        }
    }
}