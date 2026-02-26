package com.mayank.knowledgeplatform.service;

import com.mayank.knowledgeplatform.dto.AIRequest;
import com.mayank.knowledgeplatform.dto.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.model}")
    private String groqApiModel;

    public AIResponse improveContent(AIRequest request) {
        try {
            String prompt = "Please improve the grammar, structure, and professional tone of the following text:\n\n"
                    + request.getContent();
            return callGroqAPI(prompt);
        } catch (Exception e) {
            System.err.println("Groq API call failed, using fallback: " + e.getMessage());
            return new AIResponse("Mocked Improved Content: " + request.getContent() + " (Improved version)");
        }
    }

    public AIResponse summarizeContent(AIRequest request) {
        try {
            String prompt = "Provide a short, 2-3 line summary of the following text:\n\n" + request.getContent();
            return callGroqAPI(prompt);
        } catch (Exception e) {
            System.err.println("Groq API call failed, using fallback: " + e.getMessage());
            return new AIResponse("Mocked Summary: A summary of the provided text.");
        }
    }

    public AIResponse suggestTags(AIRequest request) {
        try {
            String prompt = "Suggest 3-5 relevant comma-separated tags for the following article text:\n\n"
                    + request.getContent();
            return callGroqAPI(prompt);
        } catch (Exception e) {
            System.err.println("Groq API call failed, using fallback: " + e.getMessage());
            return new AIResponse("java, spring boot, ai");
        }
    }

    @SuppressWarnings("unchecked")
    private AIResponse callGroqAPI(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", groqApiModel);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(groqApiUrl, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, String> msg = (Map<String, String>) choice.get("message");
                if (msg != null && msg.containsKey("content")) {
                    return new AIResponse(msg.get("content").trim());
                }
            }
        }

        throw new RuntimeException("Failed to extract response from GroqCloud API");
    }
}
