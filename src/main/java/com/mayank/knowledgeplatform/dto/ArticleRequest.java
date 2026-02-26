package com.mayank.knowledgeplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArticleRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String category;
    private String tags;
}
