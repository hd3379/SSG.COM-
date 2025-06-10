package com.ssg_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SsgProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsgProjectApplication.class, args);
    }

}
