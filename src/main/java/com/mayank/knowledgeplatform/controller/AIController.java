package com.mayank.knowledgeplatform.controller;

import com.mayank.knowledgeplatform.dto.AIRequest;
import com.mayank.knowledgeplatform.dto.AIResponse;
import com.mayank.knowledgeplatform.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/improve")
    public ResponseEntity<AIResponse> improveContent(@Valid @RequestBody AIRequest request) {
        AIResponse response = aiService.improveContent(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/summary")
    public ResponseEntity<AIResponse> summarizeContent(@Valid @RequestBody AIRequest request) {
        AIResponse response = aiService.summarizeContent(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/suggest-tags")
    public ResponseEntity<AIResponse> suggestTags(@Valid @RequestBody AIRequest request) {
        AIResponse response = aiService.suggestTags(request);
        return ResponseEntity.ok(response);
    }
}
