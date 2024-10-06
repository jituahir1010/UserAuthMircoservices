package org.example.for_doc1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ForDoc1Application {

    public static void main(String[] args) {
        SpringApplication.run(ForDoc1Application.class, args);
    }

}
