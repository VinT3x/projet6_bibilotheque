package com.oc.projet3.bibliows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class BibliowsApplication implements ApplicationRunner {

    private static final Logger logger = LogManager.getLogger(BibliowsApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(BibliowsApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        logger.info("Started BibliowsApplication");

    }
}

