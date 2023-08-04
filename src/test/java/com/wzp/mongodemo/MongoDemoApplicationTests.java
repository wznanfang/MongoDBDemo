package com.wzp.mongodemo;

import com.wzp.mongodemo.entity.Book;
import com.wzp.mongodemo.entity.BookInfo;
import com.wzp.mongodemo.repository.BookRepository;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@SpringBootTest
class MongoDemoApplicationTests {

    @Resource
    private BookRepository bookRepository;
    @Resource
    private MongoTemplate mongoTemplate;


    @Test
    void jpaSave() {
        Book book = new Book();
        book.setId(0L);
        book.setCreateDate(LocalDateTime.now());
        book.setName("人间游途");
        BookInfo bookInfo = BookInfo.builder().author("我是不逆").price(0.0).build();
        book.setBookInfo(bookInfo);
        bookRepository.save(book);
        List<Book> list = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            Book book1 = new Book();
            book1.setId((long) i);
            book1.setCreateDate(LocalDateTime.now());
            book1.setName("人间游途" + i);
            BookInfo bookInfo1 = BookInfo.builder().author("我是不逆" + i).price((double) i).build();
            book1.setBookInfo(bookInfo1);
            list.add(book1);
        }
        bookRepository.saveAll(list);
    }


    @Test
    void jpaUpdate() {
        Optional<Book> optional = bookRepository.findById("ab99660d-aecf-435e-b7dc-a96496d5628b");
        Book book = optional.orElse(new Book());
        BookInfo bookInfo = book.getBookInfo();
        bookInfo.setPrice(99.99);
        book.setBookInfo(bookInfo);
        bookRepository.save(book);
    }


    @Test
    void jpaDelete() {
//        bookRepository.deleteById("ab99660d-aecf-435e-b7dc-a96496d5628b");
        bookRepository.deleteAll();
    }


    @Test
    void jpaFindById() {
        Optional<Book> optional = bookRepository.findById("88f8d913-9ebd-4765-9ec3-a6b6c0f1bde7");
        System.err.println(optional.orElse(null));
    }


    @Test
    void jpaFindAll() {
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher
                .matching()//构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)//模糊查询
                .withIgnoreCase(true);//忽略大小写
        Book book = new Book();
        book.setName("人间");
        book.setBookInfo(BookInfo.builder().author("我是").build());
        Sort sort = Sort.by(Sort.Order.asc("price"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        Example<Book> example = Example.of(book, matcher);
        Page<Book> page = bookRepository.findAll(example, pageable);
        if (!CollectionUtils.isEmpty(page.getContent())) {
            page.getContent().forEach(System.out::println);
        }
    }


    /***********************************-----使用mongoTemplate-----***********************************/


    @Test
    void templateSave() {
        Book book = new Book();
        book.setId(0L);
        book.setCreateDate(LocalDateTime.now());
        book.setName("人间游途");
        BookInfo bookInfo = BookInfo.builder().author("我是不逆").price(0.0).build();
        book.setBookInfo(bookInfo);
        mongoTemplate.insert(book);
        List<Book> list = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            Book book1 = new Book();
            book1.setId((long) i);
            book1.setCreateDate(LocalDateTime.now());
            book1.setName("人间游途" + i);
            BookInfo bookInfo1 = BookInfo.builder().author("我是不逆" + i).price((double) i).build();
            book.setBookInfo(bookInfo1);
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
            criteria.and("bookInfo.author").regex(authorPattern);
        }
        Query query = new Query(criteria).skip(0).limit(10).with(Sort.by(Sort.Order.asc("bookInfo.price")));
        //查询数来集合（表）中的总记录数
        long count = mongoTemplate.count(query, Book.class);
        List<Book> books = mongoTemplate.find(query, Book.class);
        if (!CollectionUtils.isEmpty(books)) {
            books.forEach(System.out::println);
        }
    }


    @Test
    void aggr() {
        //求平均数
//        GroupOperation groupOperation = group().avg("price").as("averagePrice");
//        Aggregation aggregation = newAggregation(groupOperation);
//        // 执行聚合操作
//        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "book", Document.class);
//        Document aggregateResult = result.getUniqueMappedResult();
//        // 提取平均价格值
//        Double averagePrice = aggregateResult.getDouble("averagePrice");
//        System.err.println(averagePrice);

        //求分组后的总数
        GroupOperation groupOperation = group("author").sum("price").as("totalPrice");
        LimitOperation limitOperation = limit(10);
        Aggregation aggregation = newAggregation(groupOperation, limitOperation);
        //执行聚合操作
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "book", Document.class);
        result.getMappedResults().forEach(System.out::println);
    }


}
