package com.knowledgeassistant.backend.repository;

import com.knowledgeassistant.backend.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByUserEmailOrderByTimestampAsc(String userEmail);

}