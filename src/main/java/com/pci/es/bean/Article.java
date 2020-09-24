package com.pci.es.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zyting
 * @sinne 2020-09-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    private long id;
    private String title;
    private String content;

}
