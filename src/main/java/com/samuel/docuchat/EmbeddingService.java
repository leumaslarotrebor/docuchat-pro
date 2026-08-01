package com.samuel.docuchat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String OLLAMA_URL = "http://localhost:11434/api/embeddings";

    public List<Float> embed(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
            "{\"model\": \"nomic-embed-text\", \"prompt\": %s}",
            objectMapper.valueToTree(text).toString()
        );

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(OLLAMA_URL, request, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.get("embedding");
            List<Float> result = new ArrayList<>();
            for (JsonNode val : embeddingNode) {
                result.add(val.floatValue());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse embedding response", e);
        }
    }

    public String embedAsString(String text) {
        List<Float> vector = embed(text);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(vector.get(i));
        }
        return sb.toString();
    }
}
