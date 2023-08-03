package com.wzp.mongodemo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author zp.wei
 * @date 2023/8/2 11:56
 */
@Data
@Document(value = "book")
public class Book {

    @Id
    private String id;
    private LocalDateTime createDate;
    private String name;
    private String author;
    private Double price;
    private Books books;
    
}
