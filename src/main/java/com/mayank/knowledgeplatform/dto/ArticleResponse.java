package com.mayank.knowledgeplatform.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String category;
    private String tags;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
