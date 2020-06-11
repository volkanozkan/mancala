package io.github.volkanozkan.mancala.service;

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

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.data.mongodb.database=mancalaTestDB" })
public class PlayerServiceTest {

	@Autowired
	PlayerService playerService;
	@Autowired
	GameService gameService;

	@Mock
	private WebSocketSession webSocketSession;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		playerService.deleteAllPlayers();
	}

	@Test
	public void testCreateNewPlayer() {
		when(webSocketSession.getId()).thenReturn("1");
		Player player = playerService.createNewPlayer("Test Player", webSocketSession.getId(), UUID.randomUUID());
		Assert.assertNotNull(player);
	}

	@Test
	public void shouldGetPlayerBySessionId() {
		Player nullPlayer = playerService.getPlayerBySessionId("300");
		Assert.assertNull(nullPlayer);

		when(webSocketSession.getId()).thenReturn("2");
		Player actualPlayer = playerService.createNewPlayer("Test User", webSocketSession.getId(), UUID.randomUUID());
		Player expectedPlayer = playerService.getPlayerBySessionId(webSocketSession.getId());
		Assert.assertNotNull(expectedPlayer);
		Assert.assertEquals(actualPlayer, expectedPlayer);
	}

	@Test
	public void testFindOpponent() {
		gameService.deleteAllGames();

		Game game = gameService.createOrFindAvailableGame();
		Player player = playerService.createNewPlayer("Test", "123", game.getGameId());
		game.setFirstPlayer(player);
		gameService.updateGame(game);

		Assert.assertNull(playerService.getPlayersOpponent(game.getFirstPlayer()));

		Player secondPlayer = playerService.createNewPlayer("Test", "123456", game.getGameId());

		game.setSecondPlayer(secondPlayer);
		gameService.updateGame(game);

		Player opponent = playerService.getPlayersOpponent(game.getFirstPlayer());

		Assert.assertNotNull(opponent);
		Assert.assertEquals(secondPlayer, opponent);
	}
}
