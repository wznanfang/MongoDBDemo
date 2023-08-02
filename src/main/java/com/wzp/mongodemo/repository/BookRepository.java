package com.wzp.mongodemo.repository;

import com.wzp.mongodemo.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author zp.wei
 * @date 2023/8/2 11:58
 */
public interface BookRepository extends MongoRepository<Book, String> {


}
