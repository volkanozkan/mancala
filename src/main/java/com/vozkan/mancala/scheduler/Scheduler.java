package com.vozkan.mancala.scheduler;

import com.vozkan.mancala.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class Scheduler {
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	private final GameService gameService;

	public Scheduler(GameService gameService) {
		this.gameService = gameService;
	}

	/**
	 * After Every 30 minutes(1800000 ms) this method will terminate the games that
	 * started at least 1 hour ago.
	 */
	@Scheduled(fixedRate = 1800000)
	public void gameSweeper() {
		log.info("Game sweeper is running. {}", LocalDateTime.now());
		gameService.terminateAllLegacyGames();
	}

	@Bean
	public TaskScheduler poolScheduler() {
		return new ThreadPoolTaskScheduler();
	}
}
