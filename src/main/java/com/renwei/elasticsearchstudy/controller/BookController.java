package com.renwei.elasticsearchstudy.controller;

import com.renwei.elasticsearchstudy.dao.BookDao;
import com.renwei.elasticsearchstudy.entity.Book;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * @desc
 * @autor renw
 * @date 2018/8/14 上午10:42
 */
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookDao bookDao;

    @PostMapping("/add")
    public Book addBook(@RequestBody Book book) {
        bookDao.save(book);
        return book;
    }

    @GetMapping("/get/{id}")
    public Book getBookById(@PathVariable String id) {
        return bookDao.findById(id).get();
    }

    @DeleteMapping("/delete/{id}")
    public Book deleteBookById(@PathVariable String id) {
        Book book = bookDao.findById(id).get();
        if(book != null) {
            bookDao.delete(book);
        }
        return book;
    }

    @PutMapping("/update")
    public Book updateBook(@RequestBody Book book) {
        return bookDao.save(book);
    }

    @GetMapping("/all")
    public List<Book> searchAll() {
        Iterable<Book> books = bookDao.findAll();
        List<Book> bookList = new ArrayList<Book>();
        books.forEach(single ->{bookList.add(single);});
        return bookList;
    }

    @GetMapping("/{page}/{size}/{q}")
    public List<Book> search(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String q) {
        //TODO spring boot 2.0.4 ES分页处理有问题，一直查询不到数据，可能是还没有找到正确的方法 - 2018-08-18
        Pageable pageable = new PageRequest(page, size);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.
                functionScoreQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q)),
                        ScoreFunctionBuilders.weightFactorFunction(1000));
//                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q)),
//                        ScoreFunctionBuilders.weightFactorFunction(1000))
//                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("message", q)),
//                        ScoreFunctionBuilders.weightFactorFunction(100));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(
                QueryBuilders.matchQuery("name", q));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                //.withPageable(pageable)
                .withQuery(matchAllQuery())
               // .withFilter(QueryBuilders.boolFilter().must(termFilter("id", documentId)))
                .build();
        
        Page<Book> bookPageResult = bookDao.search(searchQuery);
        return bookPageResult.getContent();
    }
}
