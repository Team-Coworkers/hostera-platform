package com.teamcoworkers.hostera.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point of the Hostera Platform RESTful API.
 */
@EnableJpaAuditing
@SpringBootApplication
public class HosteraPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HosteraPlatformApplication.class, args);
    }

}
