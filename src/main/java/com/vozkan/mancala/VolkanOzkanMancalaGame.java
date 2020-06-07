package com.vozkan.mancala;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class VolkanOzkanMancalaGame {
	private static final Logger logger = LoggerFactory.getLogger(VolkanOzkanMancalaGame.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(VolkanOzkanMancalaGame.class);

		Environment environment = app.run(args).getEnvironment();
		logger.info("-------------Application started on http://localhost:{}-------------",
				environment.getProperty("server.port"));
	}

}
