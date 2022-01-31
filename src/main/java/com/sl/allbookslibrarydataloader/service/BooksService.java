package com.sl.allbookslibrarydataloader.service;

import com.sl.allbookslibrarydataloader.dal.domain.Author;
import com.sl.allbookslibrarydataloader.dal.domain.Book;
import com.sl.allbookslibrarydataloader.dal.repository.AuthorRepository;
import com.sl.allbookslibrarydataloader.dal.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

@Service
@Slf4j
public class BooksService {

    @Autowired
    private BookRepository bookRepository;

    @Value("${dataDump.location.books}")
    private String booksDumpLocation;

    @Autowired
    private AuthorRepository authorRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");


    public void addBooks() {

        // Check if already loaded. If yes, don't load again
        if(bookRepository.count() > 50000 ) {
            return;
        }

        log.info("> dump location: {}", booksDumpLocation);

        Path filePath = Paths.get(booksDumpLocation);

        try(Stream<String> authorsLines = lines(filePath)) {
            authorsLines.forEach(aLine -> {
                String jsonString = aLine.substring(aLine.indexOf("{"));
                log.info(jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    Book bookObj = Book.builder()
                            .name(jsonObject.optString("title"))
                            .id(jsonObject.optString("key").replace("/works/", ""))
                            .build();

                    // Get Description
                    JSONObject bookDesc = jsonObject.optJSONObject("description");
                    if(null != bookDesc ) {
                        bookObj.setDescription(jsonObject.optString("description"));
                    }

                    // Get publish date
                    JSONObject publishDateJsnObj = jsonObject.optJSONObject("created");
                    if(null != publishDateJsnObj ) {
                        bookObj.setPublishedDate(LocalDate.parse(publishDateJsnObj.getString("value"), DATE_TIME_FORMATTER));
                    }

                    // Covers
                    JSONArray coversArray = jsonObject.optJSONArray("covers");
                    if(null != coversArray) {
                        List<String> coverIds = new ArrayList<>();
                        for (int i = 0; i < coversArray.length(); i++) {
                            coverIds.add(coversArray.getString(i));
                        }
                        bookObj.setCoverIds(coverIds);
                    }

                    // Authors ids and names
                    JSONArray authorsArray = jsonObject.optJSONArray("authors");
                    if(null != authorsArray) {
                        List<String> authorIds = new ArrayList<>();
                        for (int i = 0; i < authorsArray.length(); i++) {
                            authorIds.add(authorsArray.optJSONObject(i).getJSONObject("author").getString("key")
                                    .replace("/authors/","")
                                    .replace("/a/",""));
                        }
                        bookObj.setAuthorIds(authorIds);

                        bookObj.setAuthorNames(
                                authorIds.stream().map(id ->authorRepository.findById(id))
                                .map(optionalAuthor -> {
                                    if (optionalAuthor.isPresent()) return optionalAuthor.get().getName();
                                    else return "Unknown Author";
                                })
                                .collect(Collectors.toList()));
                    }
                    log.info(bookObj.toString());
                    bookRepository.save(bookObj);

                } catch (JSONException e) {
                    log.error("> Unable to parse json string {}", jsonString, e);
                }
            });
        } catch (IOException e) {
            log.error("> Unable to parse json file {}", e.getMessage(), e);
        }
        log.info("> Done persisting books to db");


    }
}
