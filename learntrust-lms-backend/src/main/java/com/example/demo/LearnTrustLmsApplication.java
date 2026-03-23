package com.example.demo;

//it contains the run() method that starts everything. It also has the @SpringBootApplication annotation, which is a convenience annotation that adds @Configuration, @EnableAutoConfiguration, and @ComponentScan.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//These annotations are used to specify the packages where Spring should look for JPA entities and repositories. This is necessary because our entities and repositories are in different packages than the main application class.

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.demo.entity")
@EnableJpaRepositories("com.example.demo.repository")
public class LearnTrustLmsApplication {
    
    //entry point of the application. When you run the application, this method will be executed, and it will start the Spring Boot application context.
    public static void main(String[] args) {
        SpringApplication.run(LearnTrustLmsApplication.class, args);
    }
}

