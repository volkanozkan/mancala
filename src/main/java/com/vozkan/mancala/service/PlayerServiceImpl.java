package com.vozkan.mancala.service;

import com.vozkan.mancala.model.Player;
import com.vozkan.mancala.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerServiceImpl implements PlayerService {

	private final PlayerRepository playerRepository;

	public PlayerServiceImpl(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	@Override
	public Player createNewPlayer(String playerName, String sessionId, UUID gameId) {
		return playerRepository.createNewPlayer(playerName, sessionId, gameId);
	}

	@Override
	public Player getPlayerBySessionId(String sessionId) {
		return playerRepository.getPlayerBySessionId(sessionId);
	}

	@Override
	public Player getPlayersOpponent(Player player) {
		return playerRepository.getPlayersOpponent(player);
	}

	@Override
	public void deleteAllPlayers() {
		playerRepository.deleteAllPlayers();
	}

}
