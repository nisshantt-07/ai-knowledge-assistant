package com.knowledgeassistant.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PythonServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String PYTHON_SERVICE_URL = "http://localhost:8000";

    public Map<String, Object> getChunks(String text) {
        String url = PYTHON_SERVICE_URL + "/chunk";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("chunk_size", 500);
        requestBody.put("overlap", 100);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, requestEntity, Map.class);
    }

    public Map<String, Object> storeDocument(String text, Long documentId) {
        String url = PYTHON_SERVICE_URL + "/store";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("document_id", documentId);
        requestBody.put("chunk_size", 500);
        requestBody.put("overlap", 100);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, requestEntity, Map.class);
    }

    public Map<String, Object> askQuestion(String question) {
        String url = PYTHON_SERVICE_URL + "/answer";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("question", question);
        requestBody.put("top_k", 3);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, requestEntity, Map.class);
    }
}