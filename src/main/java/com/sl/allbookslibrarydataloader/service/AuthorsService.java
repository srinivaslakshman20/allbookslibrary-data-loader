package com.sl.allbookslibrarydataloader.service;

import com.sl.allbookslibrarydataloader.dal.domain.Author;
import com.sl.allbookslibrarydataloader.dal.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

@Service
@Slf4j
public class AuthorsService {

    @Value("${dataDump.location.authors}")
    private String authorsDumpLocation;

    @Autowired
    private AuthorRepository authorRepository;

    public void addAuthors() {

        // Check if already loaded. If yes, don't load again
        if(authorRepository.count() > 50000 ) {
            return;
        }

        log.info("> dump location: {}", authorsDumpLocation);

        Path filePath = Paths.get(authorsDumpLocation);

        try(Stream<String> authorsLines = lines(filePath)) {
            authorsLines.forEach(aLine -> {
                String jsonString = aLine.substring(aLine.indexOf("{"));
                log.info(jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    authorRepository.save(Author.builder()
                            .name(jsonObject.optString("name"))
                            .personalName(jsonObject.optString("personal_name"))
                            .id(jsonObject.optString("key").replace("/a/", "").replace("/authors/", ""))
                            .build());

                } catch (JSONException e) {
                    log.error("> Unable to parse json string {}", jsonString);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("> Done persisting authors to db");
    }
}
