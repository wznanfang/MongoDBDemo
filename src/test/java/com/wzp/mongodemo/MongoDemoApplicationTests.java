package com.wzp.mongodemo;

import com.wzp.mongodemo.entity.Book;
import com.wzp.mongodemo.repository.BookRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@SpringBootTest
class MongoDemoApplicationTests {

    @Resource
    private BookRepository bookRepository;
    @Resource
    private MongoTemplate mongoTemplate;


    @Test
    void jpaSave() {
        Book book = new Book();
        book.setId(UUID.randomUUID().toString());
        book.setCreateDate(LocalDateTime.now());
        book.setName("人间游途");
        book.setAuthor("不逆");
        book.setPrice(BigDecimal.valueOf(100));
        bookRepository.save(book);
        List<Book> list = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            Book book1 = new Book();
            book1.setId(UUID.randomUUID().toString());
            book1.setCreateDate(LocalDateTime.now());
            book1.setName("人间游途" + i);
            book1.setAuthor("不逆" + i);
            book1.setPrice(BigDecimal.valueOf(100 + i));
            list.add(book1);
        }
        bookRepository.saveAll(list);
    }


    @Test
    void jpaUpdate() {
        Optional<Book> optional = bookRepository.findById("ab99660d-aecf-435e-b7dc-a96496d5628b");
        Book book = optional.orElse(new Book());
        book.setPrice(BigDecimal.valueOf(99.99));
        bookRepository.save(book);
    }


    @Test
    void jpaDelete() {
//        bookRepository.deleteById("ab99660d-aecf-435e-b7dc-a96496d5628b");
        bookRepository.deleteAll();
    }


    @Test
    void jpaFindById() {
        Optional<Book> optional = bookRepository.findById("ab99660d-aecf-435e-b7dc-a96496d5628b");
        System.err.println(optional.orElse(null));
    }


    @Test
    void jpaFindAll() {
        ///创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher
                .matching()//构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true);//改变默认大小写忽略方式：忽略大小写
        Book book = new Book();
        book.setName("人间");
        book.setAuthor("不");
        Sort sort = Sort.by(Sort.Order.asc("price"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        Example<Book> example = Example.of(book, matcher);
        Page<Book> page = bookRepository.findAll(example, pageable);
        if (!CollectionUtils.isEmpty(page.getContent())) {
            page.getContent().forEach(System.out::println);
        }
    }


    /**************************-----使用mongoTemplate-----******************************/


    @Test
    void templateSave() {
        Book book = new Book();
        book.setId(UUID.randomUUID().toString());
        book.setCreateDate(LocalDateTime.now());
        book.setName("人间游途");
        book.setAuthor("不逆");
        book.setPrice(BigDecimal.valueOf(100));
        mongoTemplate.insert(book);
        List<Book> list = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            Book book1 = new Book();
            book1.setId(UUID.randomUUID().toString());
            book1.setCreateDate(LocalDateTime.now());
            book1.setName("人间游途" + i);
            book1.setAuthor("不逆" + i);
            book1.setPrice(BigDecimal.valueOf(100 + i));
            list.add(book1);
        }
        mongoTemplate.insertAll(list);
    }


    @Test
    void templateUpdate() {
        Query query = new Query(Criteria.where("_id").is("560bf261-ea53-4fb3-8f7f-33380f54f6ef"));
        Update update = new Update();
        update.set("price", 100);
        mongoTemplate.updateFirst(query, update, Book.class);
    }


    @Test
    void templateDelete() {
//        Query query = new Query(Criteria.where("_id").is("560bf261-ea53-4fb3-8f7f-33380f54f6ef"));// 单条删除
        Query query = new Query(Criteria.where("_id").in("560bf261-ea53-4fb3-8f7f-33380f54f6ef"));
        mongoTemplate.remove(query, Book.class);
    }


    @Test
    void templateFindById() {
        Book book = mongoTemplate.findById("560bf261-ea53-4fb3-8f7f-33380f54f6ef", Book.class);
        System.err.println(book);
    }


    @Test
    void templateFindAll() {
        //不带条件查询全部
//        List<Book> bookList = mongoTemplate.findAll(Book.class);
//        System.err.println(bookList);
        //带条件模糊查询
        String name = "人间";
        String author = "不逆";
        //查询条件构建
        Criteria criteria = new Criteria();
        if (!ObjectUtils.isEmpty(name)) {
            Pattern namePattern = Pattern.compile(".*" + name + ".*", Pattern.CASE_INSENSITIVE);
            criteria.and("name").regex(namePattern);
        }
        if (!ObjectUtils.isEmpty(author)) {
            Pattern authorPattern = Pattern.compile(".*" + author + ".*", Pattern.CASE_INSENSITIVE);
            criteria.and("author").regex(authorPattern);
        }
        Query query = new Query(criteria).skip(0).limit(10).with(Sort.by(Sort.Order.desc("price")));
        //查询数来集合（表）中的总记录数
        long count = mongoTemplate.count(query, Book.class);
        List<Book> books = mongoTemplate.find(query, Book.class);
        if (!CollectionUtils.isEmpty(books)) {
            books.forEach(System.out::println);
        }
    }


}
