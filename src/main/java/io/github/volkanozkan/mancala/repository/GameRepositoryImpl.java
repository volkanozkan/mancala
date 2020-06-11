package io.github.volkanozkan.mancala.repository;

import io.github.volkanozkan.mancala.model.Constants;
import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class GameRepositoryImpl implements GameRepository {
	private static final Logger logger = LoggerFactory.getLogger(GameRepositoryImpl.class);

	private final MongoTemplate mongoTemplate;

	public GameRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<Game> listAllGames() {
		return mongoTemplate.findAll(Game.class);
	}

	@Override
	public List<Game> listAvailableGames() {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameStatus").is(GameStatus.PENDING_OPPONENT));
		return mongoTemplate.find(query, Game.class);
	}

	@Override
	public Game createNewGame() {
		logger.debug("Creating new game");
		Game game = new Game();
		mongoTemplate.insert(game, Constants.GAME_COLLECTION_NAME);
		logger.debug("Game created {}", game);

		return game;
	}

	@Override
	public Game updateGame(Game game) {
		return mongoTemplate.save(game, Constants.GAME_COLLECTION_NAME);
	}

	@Override
	public Game getGameById(UUID uuid) {
		return mongoTemplate.findById(uuid, Game.class);
	}

	@Override
	// this method is mostly for test cases
	public void deleteAllGames() {
		mongoTemplate.remove(new Query(), Game.class);
		logger.info("All games are deleted");
	}

	@Override
	public List<Game> findUnfinishedGames() {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameStatus").in(GameStatus.PENDING_OPPONENT, GameStatus.PLAYER_ONE_TURN,
				GameStatus.PLAYER_TWO_TURN));
		return mongoTemplate.find(query, Game.class);
	}

}
