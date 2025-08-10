package com.epam.gymcrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.epam.gymcrm")
@EnableJpaAuditing
public class GymCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymCrmApplication.class, args);
    }
}
