package com.renwei.elasticsearchstudy.dao;


import com.renwei.elasticsearchstudy.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @desc
 * @autor renw
 * @date 2018/8/14 上午10:39
 */
public interface BookDao extends ElasticsearchRepository<Book, String> {

}
