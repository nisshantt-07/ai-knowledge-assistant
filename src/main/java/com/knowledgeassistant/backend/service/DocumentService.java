package com.knowledgeassistant.backend.service;

import com.knowledgeassistant.backend.entity.Document;
import com.knowledgeassistant.backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TextExtractionService textExtractionService;

    @Autowired
    private PythonServiceClient pythonServiceClient;

    private final String UPLOAD_DIR = "uploaded-documents/";

    public Document uploadDocument(MultipartFile file, String uploadedBy) throws IOException {

        // Folder na ho to bana do
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Unique filename banao
        String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(uniqueFilename);

        // File ko disk pe save karo
        Files.copy(file.getInputStream(), filePath);

        // Database me metadata save karo (status: PROCESSING)
        Document document = new Document(
                file.getOriginalFilename(),
                filePath.toString(),
                uploadedBy,
                LocalDateTime.now(),
                "PROCESSING"
        );
        document = documentRepository.save(document);

        // Ab text extract karke Python ko bhejo (chunk + embed + store)
        try {
            String text = textExtractionService.extractTextFromPdf(document.getFilePath());
            pythonServiceClient.storeDocument(text, document.getId());

            // Sab sahi raha to status READY kar do
            document.setStatus("READY");
        } catch (Exception e) {
            // Kuch galat hua to status FAILED kar do, lekin document delete mat karo
            document.setStatus("FAILED");
            System.out.println("Error processing document for AI: " + e.getMessage());
        }

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByUser(String uploadedBy) {
        return documentRepository.findByUploadedBy(uploadedBy);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}