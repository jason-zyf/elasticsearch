package com.pci.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author zyting
 * @sinne 2020-09-25
 */
@Data
@Document(indexName = "sdes_blog",type = "article")
public class Article {
    @Id
    @Field(type = FieldType.Long,store = true)
    private long id;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart")
    private String content;
}
