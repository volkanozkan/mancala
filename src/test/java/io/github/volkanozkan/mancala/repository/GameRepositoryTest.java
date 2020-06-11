package io.github.volkanozkan.mancala.repository;

import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.GameStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.data.mongodb.database=mancalaTestDB" })
public class GameRepositoryTest {

	@Autowired
	GameRepository gameRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameRepository.deleteAllGames();
	}

	@Test
	public void testCreateGame() {
		Game game = gameRepository.createNewGame();
		Assert.assertNotNull(game);

		Game findGame = gameRepository.getGameById(game.getGameId());
		Assert.assertEquals(game.getGameId(), findGame.getGameId());
		Assert.assertEquals(game.getStartedAt(), findGame.getStartedAt());
	}

	@Test
	public void testListAllGames() {
		gameRepository.createNewGame();
		gameRepository.createNewGame();
		gameRepository.createNewGame();
		gameRepository.createNewGame();

		int allGames = gameRepository.listAllGames().size();
		Assert.assertEquals(4, allGames);
	}

	@Test
	public void testUpdateGame() {
		Game game = gameRepository.createNewGame();

		game.setFinishedAt(LocalDateTime.now());
		game.setGameStatus(GameStatus.FINISHED);

		gameRepository.updateGame(game);

		Game findGame = gameRepository.getGameById(game.getGameId());

		Assert.assertEquals(GameStatus.FINISHED, findGame.getGameStatus());
		Assert.assertNotNull(findGame.getGameStatus().getDescription());
		Assert.assertNotNull(findGame.getFinishedAt());
		Assert.assertEquals(game.getGameId(), findGame.getGameId());
		Assert.assertEquals(game.getStartedAt(), findGame.getStartedAt());
	}

	@Test
	public void testListAvailableGamesWhenEmpty() {
		List<Game> availableGames = gameRepository.listAvailableGames();
		Assert.assertNotNull(availableGames);
		Assert.assertTrue(availableGames.isEmpty());
	}

	@Test
	public void testListAvailableGames() {
		Game game1 = gameRepository.createNewGame();
		Game game2 = gameRepository.createNewGame();
		Game game3 = gameRepository.createNewGame();
		Game game4 = gameRepository.createNewGame();

		game3.setGameStatus(GameStatus.PLAYER_ONE_TURN);
		gameRepository.updateGame(game3);

		List<Game> availableGames = gameRepository.listAvailableGames();

		Assert.assertNotNull(availableGames);
		Assert.assertFalse(availableGames.isEmpty());
		Assert.assertTrue(availableGames.size() == 3);
	}

	@Test
	public void testFindUnfinishedGamesWhenEmpty() {
		List<Game> availableGames = gameRepository.findUnfinishedGames();
		Assert.assertNotNull(availableGames);
		Assert.assertTrue(availableGames.isEmpty());
	}

	@Test
	public void testFindUnfinishedGames() {
		gameRepository.createNewGame();
		Game game1 = gameRepository.createNewGame();
		Game game2 = gameRepository.createNewGame();
		gameRepository.createNewGame();
		gameRepository.createNewGame();
		gameRepository.createNewGame();

		game1.setGameStatus(GameStatus.FINISHED);
		game2.setGameStatus(GameStatus.FINISHED_DRAW);
		gameRepository.updateGame(game1);
		gameRepository.updateGame(game2);

		List<Game> unfinishedGames = gameRepository.findUnfinishedGames();

		Assert.assertNotNull(unfinishedGames);
		Assert.assertFalse(unfinishedGames.isEmpty());
		Assert.assertEquals(4, unfinishedGames.size());
	}

}
