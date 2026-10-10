package com.knowledgeassistant.backend.repository;

import com.knowledgeassistant.backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedBy(String uploadedBy);

}