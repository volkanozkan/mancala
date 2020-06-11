package io.github.volkanozkan.mancala.repository;

import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.data.mongodb.database=mancalaTestDB" })
public class PlayerRepositoryTest {

	@Autowired
	PlayerRepository playerRepository;
	@Autowired
	GameRepository gameRepository;

	@Mock
	private WebSocketSession webSocketSession;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		playerRepository.deleteAllPlayers();
		gameRepository.deleteAllGames();
	}

	@Test()
	public void testCreateNewPlayer() {
		Player player = playerRepository.createNewPlayer("Test", "123", UUID.randomUUID());
		Assert.assertNotNull(player);
	}

	@Test
	public void testGetPlayerBySessionId() {
		Player player = playerRepository.createNewPlayer("Test", "123", UUID.randomUUID());
		Player player2 = playerRepository.createNewPlayer("Test", "456", UUID.randomUUID());

		Player findPlayer = playerRepository.getPlayerBySessionId("123");
		Assert.assertNotNull(findPlayer);
		Assert.assertEquals(player, findPlayer);
		Assert.assertNotEquals(player2, findPlayer);

		Player findUnmatchPlayer = playerRepository.getPlayerBySessionId("9876791");
		Assert.assertNull(findUnmatchPlayer);
	}

	@Test
	public void testCantFindOpponent() {
		Game game = gameRepository.createNewGame();
		Player player = playerRepository.createNewPlayer("Test", "123", game.getGameId());
		game.setSecondPlayer(player);
		gameRepository.updateGame(game);

		Player opponent = playerRepository.getPlayersOpponent(game.getSecondPlayer());
		Assert.assertNull(opponent);
	}

	@Test
	public void testFindOpponent() {
		Game game = gameRepository.createNewGame();
		Player player = playerRepository.createNewPlayer("Test", "123", game.getGameId());
		game.setFirstPlayer(player);
		gameRepository.updateGame(game);

		Assert.assertNull(playerRepository.getPlayersOpponent(game.getFirstPlayer()));

		Player playerTwo = playerRepository.createNewPlayer("Test2", "456", game.getGameId());
		game.setSecondPlayer(playerTwo);
		gameRepository.updateGame(game);

		Player opponent = playerRepository.getPlayersOpponent(game.getSecondPlayer());
		Assert.assertNotNull(opponent);
		Assert.assertEquals(player, opponent);
	}

	@Test
	public void testFindOpponentByFirstPlayer() {
		Game game = gameRepository.createNewGame();
		Player player = playerRepository.createNewPlayer("Test", "123", game.getGameId());
		game.setFirstPlayer(player);
		gameRepository.updateGame(game);

		Assert.assertNull(playerRepository.getPlayersOpponent(game.getFirstPlayer()));

		Player playerTwo = playerRepository.createNewPlayer("Test2", "456", game.getGameId());
		game.setSecondPlayer(playerTwo);
		gameRepository.updateGame(game);

		Player opponent = playerRepository.getPlayersOpponent(game.getFirstPlayer());
		Assert.assertNotNull(opponent);
		Assert.assertEquals(playerTwo, opponent);
	}
}
