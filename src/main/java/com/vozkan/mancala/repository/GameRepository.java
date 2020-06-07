package com.vozkan.mancala.repository;

import com.vozkan.mancala.model.Game;

import java.util.List;
import java.util.UUID;

public interface GameRepository {

	List<Game> listAllGames();

	List<Game> listAvailableGames();

	Game createNewGame();

	Game updateGame(Game game);

	Game getGameById(UUID uuid);

	void deleteAllGames();
	
	List<Game> findUnfinishedGames();
}