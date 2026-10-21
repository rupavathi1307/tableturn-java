package com.tableturn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** TableTurn: the waitlist and the floor of one restaurant branch. The grader starts the app through this class. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TableTurnApplication {

  public static void main(String[] args) {
        SpringApplication.run(TableTurnApplication.class, args);
    }
}