package com.pci.es.repositories;

import com.pci.es.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author zyting
 * @sinne 2020-09-25
 */
public interface ArticleRepository extends ElasticsearchRepository<Article,Long> {

    /**
     * 自定义查询，根据 ElasticsearchRepository 定义  无需实现
     * @param title
     * @return
     */
    List<Article> findByTitle(String title);

    /**
     * 标题中或者内容包含
     * @param title
     * @param content
     * @return
     */
    List<Article> findByTitleOrContent(String title,String content);

    /**
     * 标题中或者内容包含(分页查询)
     * @param title
     * @param content
     * @return
     */
    List<Article> findByTitleOrContent(String title, String content, Pageable pageable);
}
