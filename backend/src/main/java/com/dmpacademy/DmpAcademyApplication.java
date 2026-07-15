package com.dmpacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DmpAcademyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DmpAcademyApplication.class, args);
    }
}
