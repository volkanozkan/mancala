package com.vozkan.mancala.service;

import com.vozkan.mancala.model.Player;

import java.util.UUID;

public interface PlayerService {

	Player createNewPlayer(String playerName, String sessionId, UUID gameId);

	Player getPlayerBySessionId(String sessionId);

	Player getPlayersOpponent(Player player);

	void deleteAllPlayers();

}
