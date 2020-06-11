package io.github.volkanozkan.mancala.repository;

import io.github.volkanozkan.mancala.model.Player;

import java.util.UUID;

public interface PlayerRepository {

	Player createNewPlayer(String playerName, String sessionId, UUID gameId);

	Player getPlayerBySessionId(String sessionId);

	Player getPlayersOpponent(Player player);

	void deleteAllPlayers();

}
