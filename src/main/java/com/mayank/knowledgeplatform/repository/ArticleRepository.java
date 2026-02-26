package com.mayank.knowledgeplatform.repository;

import com.mayank.knowledgeplatform.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByCategoryIgnoreCase(String category);

    @Query("SELECT a FROM Article a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Article> searchArticles(@Param("keyword") String keyword);

    @Query("SELECT a FROM Article a WHERE " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(a.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND LOWER(a.category) = LOWER(:category)")
    List<Article> searchAndFilterArticles(@Param("keyword") String keyword, @Param("category") String category);
}
