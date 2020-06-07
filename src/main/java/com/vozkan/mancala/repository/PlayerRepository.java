package com.vozkan.mancala.repository;

import com.vozkan.mancala.model.Player;

import java.util.UUID;

public interface PlayerRepository {

	Player createNewPlayer(String playerName, String sessionId, UUID gameId);

	Player getPlayerBySessionId(String sessionId);

	Player getPlayersOpponent(Player player);

	void deleteAllPlayers();

}
