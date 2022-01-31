package com.sl.allbookslibrarydataloader;

import com.sl.allbookslibrarydataloader.connection.DataStaxAstraProperties;
import com.sl.allbookslibrarydataloader.dal.domain.Author;
import com.sl.allbookslibrarydataloader.dal.repository.AuthorRepository;
import com.sl.allbookslibrarydataloader.service.AuthorsService;
import com.sl.allbookslibrarydataloader.service.BooksService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
@Slf4j
public class AllbookslibraryDataLoaderApplication {

	@Autowired
	private AuthorsService authorsService;

	@Autowired
	private BooksService booksService;

	public static void main(String[] args) {
		SpringApplication.run(AllbookslibraryDataLoaderApplication.class, args);
	}

	@PostConstruct
	public void dataDump() {
		authorsService.addAuthors();
		booksService.addBooks();
	}



	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
