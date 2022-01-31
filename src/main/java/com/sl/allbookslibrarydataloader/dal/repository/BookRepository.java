package com.sl.allbookslibrarydataloader.dal.repository;

import com.sl.allbookslibrarydataloader.dal.domain.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CassandraRepository<Book, String> {
}
