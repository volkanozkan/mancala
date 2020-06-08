package com.vozkan.mancala.service;

import com.vozkan.mancala.model.Game;
import com.vozkan.mancala.model.GameStatus;
import com.vozkan.mancala.model.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.data.mongodb.database=mancalaTestDB" })
public class GameServiceTest {

	@Autowired
	PlayerService playerService;
	@Autowired
	GameService gameService;

	@Mock
	private Player player;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameService.deleteAllGames();
	}

	@Test
	public void testListAllGames() {

		List<Game> games = gameService.listAllGames();
		Assert.assertNotNull(games);
		Assert.assertTrue(games.isEmpty());

		gameService.createOrFindAvailableGame();

		List<Game> gameList = gameService.listAllGames();
		Assert.assertNotNull(gameList);
		Assert.assertFalse(gameList.isEmpty());
		Assert.assertEquals(1, gameList.size());
	}

	@Test
	public void testListAvailableGames() {
		List<Game> gameList1 = gameService.listAvailableGames();
		Assert.assertNotNull(gameList1);
		Assert.assertTrue(gameList1.isEmpty());

		Game game = gameService.createOrFindAvailableGame();

		List<Game> gameList2 = gameService.listAvailableGames();
		Assert.assertNotNull(gameList2);
		Assert.assertFalse(gameList2.isEmpty());
		Assert.assertEquals(1, gameList2.size());

		gameService.terminateGame(game.getGameId());
		List<Game> gameList3 = gameService.listAvailableGames();
		Assert.assertNotNull(gameList3);
		Assert.assertTrue(gameList3.isEmpty());
	}

	@Test
	public void testGetGameById() {
		Game game = gameService.createOrFindAvailableGame();
		Assert.assertNotNull(game);

		Game foundGame = gameService.getGameById(game.getGameId());
		Assert.assertNotNull(foundGame);
		Assert.assertNotNull(foundGame.getGameId());

		Assert.assertEquals(game.getGameId(), foundGame.getGameId());
		Assert.assertEquals(game.getStartedAt(), foundGame.getStartedAt());
	}

	@Test
	public void testTerminateGame() {
		Game game = gameService.createOrFindAvailableGame();
		Assert.assertEquals(GameStatus.PENDING_OPPONENT, game.getGameStatus());

		gameService.terminateGame(game.getGameId());

		Game findThatGame = gameService.getGameById(game.getGameId());
		Assert.assertEquals(GameStatus.FINISHED, findThatGame.getGameStatus());
		Assert.assertNotNull(findThatGame.getGameStatus().getDescription());
		Assert.assertNotNull(findThatGame.getFinishedAt());
	}

	@Test
	public void isGameOver() {
		Game game = gameService.createOrFindAvailableGame();

		Assert.assertFalse(gameService.isGameOver(game));

		game.getBoard().setFirstPlayerPits(new int[] { 0, 0, 0, 0, 0, 0 });
		gameService.updateGame(game);
		Assert.assertTrue(gameService.isGameOver(game));

		game.getBoard().setFirstPlayerPits(new int[] { 0, 7, 0, 8, 8, 8 });
		game.getBoard().setSecondPlayerPits(new int[] { 0, 0, 0, 0, 0, 0 });
		gameService.updateGame(game);
		Assert.assertTrue(gameService.isGameOver(game));
	}

	@Test
	public void testGameResultPlayer1Won() {
		Game game = gameService.createOrFindAvailableGame();

		game.getBoard().setFirstPlayerPits(new int[] { 6, 6, 0, 7, 10, 7 });
		game.getBoard().setFirstPlayerBigPit(16);

		game.getBoard().setSecondPlayerPits(new int[] { 0, 0, 0, 0, 0, 0 });
		game.getBoard().setSecondPlayerBigPit(24);

		gameService.updateGame(game);

		gameService.calculateWinner(game);

		Assert.assertArrayEquals(game.getBoard().getFirstPlayerPits(), new int[]{0, 0, 0, 0, 0, 0});
		Assert.assertArrayEquals(game.getBoard().getSecondPlayerPits(), new int[]{0, 0, 0, 0, 0, 0});

		Assert.assertEquals(52, game.getBoard().getFirstPlayerBigPit());
		Assert.assertEquals(24, game.getBoard().getSecondPlayerBigPit());

		Assert.assertEquals(GameStatus.FINISHED_P1_WON, game.getGameStatus());
	}

	@Test
	public void testGameResultPlayer2Won() {
		Game game = gameService.createOrFindAvailableGame();

		game.getBoard().setFirstPlayerPits(new int[] { 0, 0, 0, 0, 0, 0 });
		game.getBoard().setFirstPlayerBigPit(22);

		game.getBoard().setSecondPlayerPits(new int[] { 6, 6, 6, 6, 6, 6 });
		game.getBoard().setSecondPlayerBigPit(14);

		gameService.updateGame(game);
		gameService.calculateWinner(game);

		int totalStones = Arrays.stream(game.getBoard().getFirstPlayerPits()).sum()
				+ Arrays.stream(game.getBoard().getSecondPlayerPits()).sum() + game.getBoard().getFirstPlayerBigPit()
				+ game.getBoard().getSecondPlayerBigPit();
		Assert.assertEquals(72, totalStones);
		Assert.assertArrayEquals(game.getBoard().getFirstPlayerPits(), new int[]{0, 0, 0, 0, 0, 0});
		Assert.assertArrayEquals(game.getBoard().getSecondPlayerPits(), new int[]{0, 0, 0, 0, 0, 0});
		Assert.assertEquals(GameStatus.FINISHED_P2_WON, game.getGameStatus());
	}

	@Test
	public void testGameResultDraw() {
		Game game = gameService.createOrFindAvailableGame();

		game.getBoard().setFirstPlayerPits(new int[] { 0, 0, 0, 0, 0, 0 });
		game.getBoard().setFirstPlayerBigPit(36);

		game.getBoard().setSecondPlayerPits(new int[] { 1, 2, 0, 2, 5, 4 });
		game.getBoard().setSecondPlayerBigPit(22);

		gameService.updateGame(game);
		gameService.calculateWinner(game);

		Assert.assertArrayEquals(game.getBoard().getFirstPlayerPits(), new int[]{0, 0, 0, 0, 0, 0});
		Assert.assertArrayEquals(game.getBoard().getSecondPlayerPits(), new int[]{0, 0, 0, 0, 0, 0});
		Assert.assertEquals(game.getBoard().getFirstPlayerBigPit(), game.getBoard().getSecondPlayerBigPit());
		Assert.assertEquals(36, game.getBoard().getFirstPlayerBigPit());
		Assert.assertEquals(GameStatus.FINISHED_DRAW, game.getGameStatus());
	}

	@Test(expected = IllegalStateException.class)
	public void testMoveExceptionGameIsNotStarted() {
		Game game = gameService.createOrFindAvailableGame();

		gameService.move(game.getGameId(), 0, player);
	}

	@Test(expected = IllegalStateException.class)
	public void testMoveExceptionGameIsFinished() {
		Game game = gameService.createOrFindAvailableGame();
		game.setFinishedAt(LocalDateTime.now());
		gameService.updateGame(game);
		gameService.move(game.getGameId(), 0, player);
	}

	@Test(expected = IllegalStateException.class)
	public void testSecondPlayerCantMove() {
		Game game = createGame();

		gameService.move(game.getGameId(), 0, game.getSecondPlayer());
	}

	@Test(expected = IllegalStateException.class)
	public void testFirstPlayerCantMove() {
		Game game = createGame();

		gameService.move(game.getGameId(), 1, game.getFirstPlayer());
		gameService.move(game.getGameId(), 3, game.getSecondPlayer());
		gameService.move(game.getGameId(), 4, game.getFirstPlayer());
		gameService.move(game.getGameId(), 2, game.getFirstPlayer());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMovePlayerCantFound() {
		Game game = createGame();

		gameService.move(game.getGameId(), 2, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveWrongStone() {
		Game game = createGame();

		gameService.move(game.getGameId(), -4, game.getFirstPlayer());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveWrongStone2() {
		Game game = createGame();

		gameService.move(game.getGameId(), 8, game.getFirstPlayer());
	}

	@Test
	public void testMoveStonesFirstPlayerExtraTurn() {
		Game game = createGame();

		game = gameService.move(game.getGameId(), 0, game.getFirstPlayer());

		Assert.assertEquals(GameStatus.PLAYER_ONE_TURN, game.getGameStatus());
	}

	@Test
	public void testMoveStonesTurnIsOnSecondPlayer() {
		Game game = createGame();

		game = gameService.move(game.getGameId(), 4, game.getFirstPlayer());

		Assert.assertEquals(GameStatus.PLAYER_TWO_TURN, game.getGameStatus());
	}

	@Test
	public void testTotalStones() {
		Game game = createGame();

		game = gameService.move(game.getGameId(), 0, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 4, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 4, game.getSecondPlayer());
		game = gameService.move(game.getGameId(), 2, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 0, game.getSecondPlayer());
		game = gameService.move(game.getGameId(), 5, game.getFirstPlayer());

		int totalStones = Arrays.stream(game.getBoard().getFirstPlayerPits()).sum()
				+ Arrays.stream(game.getBoard().getSecondPlayerPits()).sum() + game.getBoard().getFirstPlayerBigPit()
				+ game.getBoard().getSecondPlayerBigPit();
		Assert.assertEquals(72, totalStones);
	}

	@Test
	public void testIfLastStoneLandsInEmptyPit() {
		Game game = createGame();
		game = gameService.move(game.getGameId(), 3, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 1, game.getSecondPlayer());
		game = gameService.move(game.getGameId(), 1, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 4, game.getSecondPlayer());
		game = gameService.move(game.getGameId(), 2, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 1, game.getSecondPlayer());

		int firstPlayerBigPit = game.getBoard().getFirstPlayerBigPit();

		game = gameService.move(game.getGameId(), 1, game.getFirstPlayer());
		int firstPlayerNewBigPit = game.getBoard().getFirstPlayerBigPit();

		Assert.assertTrue(firstPlayerNewBigPit > firstPlayerBigPit);

		int totalStones = Arrays.stream(game.getBoard().getFirstPlayerPits()).sum()
				+ Arrays.stream(game.getBoard().getSecondPlayerPits()).sum() + game.getBoard().getFirstPlayerBigPit()
				+ game.getBoard().getSecondPlayerBigPit();
		Assert.assertEquals(72, totalStones);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveNoStones() {
		Game game = createGame();

		game = gameService.move(game.getGameId(), 0, game.getFirstPlayer());
		game = gameService.move(game.getGameId(), 0, game.getFirstPlayer());

		Assert.assertEquals(GameStatus.PLAYER_ONE_TURN, game.getGameStatus());
	}

	@Test
	public void testTerminateAllLegacyGames() {
		Game game1 = gameService.createOrFindAvailableGame();
		game1.setGameStatus(GameStatus.PLAYER_ONE_TURN);
		gameService.updateGame(game1);

		Game game2 = gameService.createOrFindAvailableGame();
		game2.setGameStatus(GameStatus.OPPONENT_LEFT);
		gameService.updateGame(game2);

		Game game3 = gameService.createOrFindAvailableGame();

		List<Game> games = gameService.listAllGames();
		Assert.assertNotNull(games);
		Assert.assertEquals(3, games.size());

		List<Game> availableGames = gameService.listAvailableGames();
		Assert.assertNotNull(availableGames);
		Assert.assertEquals(1, availableGames.size());

		gameService.terminateAllLegacyGames();

		availableGames = gameService.listAvailableGames();
		Assert.assertEquals(1, availableGames.size());

		game3.setStartedAt(LocalDateTime.now().minusHours(1));
		gameService.updateGame(game3);

		gameService.terminateAllLegacyGames();

		games = gameService.listAllGames();
		Assert.assertEquals(3, games.size());
		availableGames = gameService.listAvailableGames();
		Assert.assertTrue(availableGames.isEmpty());
	}

	private Game createGame() {
		Game game = gameService.createOrFindAvailableGame();
		Player firstPlayer = new Player("Test1Player", "123", game.getGameId());
		Player secondPlayer = new Player("Test2Player", "456", game.getGameId());
		game.setFirstPlayer(firstPlayer);
		game.setSecondPlayer(secondPlayer);
		game.setGameStatus(GameStatus.PLAYER_ONE_TURN);
		gameService.joinNewGameAsFirstPlayer(game, firstPlayer);
		gameService.joinToExistingGame(game, secondPlayer);
		return game;
	}

}
