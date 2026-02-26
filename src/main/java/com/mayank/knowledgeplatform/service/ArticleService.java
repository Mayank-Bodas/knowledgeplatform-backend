package com.mayank.knowledgeplatform.service;

import com.mayank.knowledgeplatform.dto.ArticleRequest;
import com.mayank.knowledgeplatform.dto.ArticleResponse;
import com.mayank.knowledgeplatform.exception.AccessDeniedException;
import com.mayank.knowledgeplatform.exception.ResourceNotFoundException;
import com.mayank.knowledgeplatform.model.Article;
import com.mayank.knowledgeplatform.model.User;
import com.mayank.knowledgeplatform.repository.ArticleRepository;
import com.mayank.knowledgeplatform.repository.UserRepository;
import com.mayank.knowledgeplatform.dto.AIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    public List<ArticleResponse> getAllArticles(String keyword, String category) {
        List<Article> articles;

        if (keyword != null && !keyword.isEmpty() && category != null && !category.isEmpty()) {
            articles = articleRepository.searchAndFilterArticles(keyword, category);
        } else if (keyword != null && !keyword.isEmpty()) {
            articles = articleRepository.searchArticles(keyword);
        } else if (category != null && !category.isEmpty()) {
            articles = articleRepository.findByCategoryIgnoreCase(category);
        } else {
            articles = articleRepository.findAll();
        }

        return articles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        return mapToResponse(article);
    }

    public ArticleResponse createArticle(ArticleRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        String generatedSummary = "No summary available.";
        try {
            generatedSummary = aiService.summarizeContent(new AIRequest(request.getContent())).getResult();
        } catch (Exception e) {
            System.err.println("Failed to generate summary: " + e.getMessage());
        }

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .summary(generatedSummary)
                .category(request.getCategory())
                .tags(request.getTags())
                .author(author)
                .build();

        return mapToResponse(articleRepository.save(article));
    }

    public ArticleResponse updateArticle(Long id, ArticleRequest request, String userEmail) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!article.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not authorized to update this article");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        try {
            String newSummary = aiService.summarizeContent(new AIRequest(request.getContent())).getResult();
            article.setSummary(newSummary);
        } catch (Exception e) {
            System.err.println("Failed to update summary: " + e.getMessage());
        }

        article.setCategory(request.getCategory());
        article.setTags(request.getTags());

        return mapToResponse(articleRepository.save(article));
    }

    public void deleteArticle(Long id, String userEmail) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!article.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not authorized to delete this article");
        }

        articleRepository.delete(article);
    }

    private ArticleResponse mapToResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .summary(article.getSummary())
                .category(article.getCategory())
                .tags(article.getTags())
                .authorName(article.getAuthor().getUsername())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
