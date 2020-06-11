package io.github.volkanozkan.mancala.model;

import org.junit.Assert;
import org.junit.Test;

public class GameTest {

	@Test
	public void testCreateGame() {
		Game game = new Game();

		Assert.assertNotNull(game);
		Assert.assertNotNull(game.getBoard());
		Assert.assertEquals(GameStatus.PENDING_OPPONENT, game.getGameStatus());
		Assert.assertNull(game.getSecondPlayer());
		Assert.assertNotNull(game.getStartedAt());
		Assert.assertNotNull(game.getBoard());
	}

	@Test(expected = NullPointerException.class)
	public void testThrowNullPointerOnSecondPlayer() {
		Game game = new Game();
		game.getSecondPlayer().getName();
	}
}
