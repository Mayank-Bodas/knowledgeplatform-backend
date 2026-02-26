package com.mayank.knowledgeplatform.controller;

import com.mayank.knowledgeplatform.dto.ArticleRequest;
import com.mayank.knowledgeplatform.dto.ArticleResponse;
import com.mayank.knowledgeplatform.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(articleService.getAllArticles(keyword, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest articleRequest) {
        String email = getCurrentUserEmail();
        ArticleResponse created = articleService.createArticle(articleRequest, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id,
            @Valid @RequestBody ArticleRequest articleRequest) {
        String email = getCurrentUserEmail();
        ArticleResponse updated = articleService.updateArticle(id, articleRequest, email);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        articleService.deleteArticle(id, email);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.mayank.knowledgeplatform.security.UserDetailsImpl) {
            return ((com.mayank.knowledgeplatform.security.UserDetailsImpl) principal).getEmail();
        }
        return authentication.getName();
    }
}
