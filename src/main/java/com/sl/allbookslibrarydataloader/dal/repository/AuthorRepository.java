package com.sl.allbookslibrarydataloader.dal.repository;

import com.sl.allbookslibrarydataloader.dal.domain.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CassandraRepository<Author, String> {
}
