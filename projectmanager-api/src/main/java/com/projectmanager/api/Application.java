package com.projectmanager.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Application {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		
		LOGGER.info("Starting ProjectManager...");
		
		SpringApplication.run(Application.class, args);
	}
}
