package com.wzp.mongodemo.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author zp.wei
 * @date 2023/8/3 16:19
 */
@Data
@Builder
public class BookInfo {

    private String author;
    private Double price;

}
