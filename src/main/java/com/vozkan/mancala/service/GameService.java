package com.vozkan.mancala.service;

import com.vozkan.mancala.model.Game;
import com.vozkan.mancala.model.Player;

import java.util.List;
import java.util.UUID;

public interface GameService {

	List<Game> listAllGames();

	List<Game> listAvailableGames();

	Game createOrFindAvailableGame();

	void joinToExistingGame(Game game, Player player);

	void joinNewGameAsFirstPlayer(Game game, Player player);

	Game getGameById(UUID uuid);

	void terminateGame(UUID gameId);

	void deleteAllGames();

	Game move(UUID gameId, int selectedPit, Player player);

	boolean isGameOver(Game game);

	void calculateWinner(Game game);

	void terminateAllLegacyGames();

	void updateGame(Game game);

}
