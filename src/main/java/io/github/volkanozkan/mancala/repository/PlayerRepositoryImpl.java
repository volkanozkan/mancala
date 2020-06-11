package io.github.volkanozkan.mancala.repository;

import io.github.volkanozkan.mancala.model.Constants;
import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {
	private static final Logger logger = LoggerFactory.getLogger(PlayerRepositoryImpl.class);

	private final MongoTemplate mongoTemplate;
	private final GameRepository gameRepository;

	public PlayerRepositoryImpl(MongoTemplate mongoTemplate, GameRepository gameRepository) {
		this.mongoTemplate = mongoTemplate;
		this.gameRepository = gameRepository;
	}

	@Override
	public Player createNewPlayer(String playerName, String sessionId, UUID gameId) {
		Player player = new Player(playerName, sessionId, gameId);
		mongoTemplate.insert(player, Constants.PLAYER_COLLECTION_NAME);
		logger.info("New Player {} is created..", player);
		return player;
	}

	@Override
	public Player getPlayerBySessionId(String sessionId) {
		Player player = mongoTemplate.findById(sessionId, Player.class);
		logger.info("Player found by sessionId {}", player);
		return player;
	}

	@Override
	public Player getPlayersOpponent(Player player) {
		Game game = gameRepository.getGameById(player.getGameId());

		if (game != null && player.equals(game.getFirstPlayer())) {
			return game.getSecondPlayer();
		} else if (game != null && player.equals(game.getSecondPlayer())) {
			return game.getFirstPlayer();
		}

		return null; 
	}

	@Override
	// for tests
	public void deleteAllPlayers() {
		mongoTemplate.remove(new Query(), Player.class);
		logger.info("All players are deleted");
	}

}
