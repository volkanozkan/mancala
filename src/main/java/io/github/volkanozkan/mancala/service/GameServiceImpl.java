package io.github.volkanozkan.mancala.service;

import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.GameStatus;
import io.github.volkanozkan.mancala.model.Player;
import io.github.volkanozkan.mancala.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
	private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

	private final GameRepository gameRepository;

	public GameServiceImpl(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

	@Override
	public List<Game> listAllGames() {
		return gameRepository.listAllGames();
	}

	@Override
	public List<Game> listAvailableGames() {
		return gameRepository.listAvailableGames();
	}

	@Override
	public Game createOrFindAvailableGame() {
		logger.debug("Game will find or create...");

		List<Game> availableGames = gameRepository.listAvailableGames();

		if (availableGames == null || availableGames.isEmpty()) {
			return gameRepository.createNewGame();
		}

		Game availableGame = availableGames.get(0);
		logger.info("Available game found, {}", availableGame.getGameId());
		return availableGame;
	}

	@Override
	public void joinToExistingGame(Game game, Player player) {
		game.setSecondPlayer(player);
		game.setGameStatus(GameStatus.PLAYER_ONE_TURN);

		gameRepository.updateGame(game);
	}

	@Override
	public void joinNewGameAsFirstPlayer(Game game, Player player) {
		game.setFirstPlayer(player);
		gameRepository.updateGame(game);
	}

	@Override
	public Game getGameById(UUID uuid) {
		return gameRepository.getGameById(uuid);
	}

	@Override
	public void terminateGame(UUID gameId) {
		logger.info("Game {} is terminating..", gameId);
		Game game = gameRepository.getGameById(gameId);
		if (game != null && game.getFinishedAt() == null) {
			game.setFinishedAt(LocalDateTime.now());
			game.setGameStatus(GameStatus.FINISHED);
			gameRepository.updateGame(game);
			logger.info("Game terminated {}", game);
		}
	}

	@Override
	public void deleteAllGames() {
		gameRepository.deleteAllGames();
	}

	@Override
	public void terminateAllLegacyGames() {
		List<Game> games = gameRepository.findUnfinishedGames();

		games = games.stream().filter(game -> game.getStartedAt().isBefore(LocalDateTime.now().minusHours(1)))
				.collect(Collectors.toList());

		if (games != null && !games.isEmpty()) {
			for (Game game : games) {
				terminateGame(game.getGameId());
			}
		}
	}

	/**
	 * Move stones
	 * 
	 * @return game after moved stones
	 */
	@Override
	public synchronized Game move(UUID gameId, int selectedPit, Player player) {
		logger.info("Player moved {} on game {}", selectedPit, gameId);
		Game game = getGameById(gameId);

		makeMoveChecks(game, selectedPit, player);

		boolean isFirstPlayer = false;
		int[] playerPits = null;
		int[] opponentPits = null;
		int playerBigPit = 0;

		if (game.getFirstPlayer().equals(player)) {
			isFirstPlayer = true;
			playerPits = game.getBoard().getFirstPlayerPits();
			opponentPits = game.getBoard().getSecondPlayerPits();
			playerBigPit = game.getBoard().getFirstPlayerBigPit();
		} else if (game.getSecondPlayer().equals(player)) {
			playerPits = game.getBoard().getSecondPlayerPits();
			opponentPits = game.getBoard().getFirstPlayerPits();
			playerBigPit = game.getBoard().getSecondPlayerBigPit();
		}

		int stonesToMove = playerPits[selectedPit];
		if (stonesToMove <= 0) {
			throw new IllegalArgumentException("No stones on selected pit.");
		}

		int nextPit = selectedPit + 1;

		// empty selected pit
		playerPits[selectedPit] = 0;

		boolean anotherTurn = false;
		do {
			if (nextPit <= 5) {
				// when the last stone lands in an own empty pit, the player captures his own
				// stone and all stones in the opposite pit and put it and puts them in his own
				// big pit.. if not go to else case and just increment next pit by 1.
				if (playerPits[nextPit] == 0 && stonesToMove == 1) {
					int oppositePit = (6 - nextPit) - 1;
					int stonesOnOppsitePit = opponentPits[oppositePit];

					// remove stones from opposite Pit
					opponentPits[oppositePit] = 0;
					// increment players big pit for opposite pits and '1' for last stone of player
					playerBigPit = playerBigPit + stonesOnOppsitePit + 1;

				} else {
					// Increment stones on next player pit
					playerPits[nextPit] += 1;
				}
			} else if (nextPit == 6) {
				// if it is on the big pit
				playerBigPit++;

				// if last stone on big pit then get another turn
				if (stonesToMove == 1) {
					anotherTurn = true;
				}
			} else if (nextPit <= 12) {
				// when stones throughout to opponent side, put stones on the oppenent's pits.
				opponentPits[nextPit - 7] += 1;
			} else {
				// when pit have more stones than board size and get back again to
				// player's side from opponent's side
				nextPit = 0;
				continue;
			}

			nextPit++;
			stonesToMove--;
		} while (stonesToMove > 0);

		if (isFirstPlayer) {
			game.getBoard().setFirstPlayerBigPit(playerBigPit);
			game.getBoard().setFirstPlayerPits(playerPits);
			game.getBoard().setSecondPlayerPits(opponentPits);
			if (!anotherTurn) {
				game.setGameStatus(GameStatus.PLAYER_TWO_TURN);
			}
		} else {
			game.getBoard().setSecondPlayerBigPit(playerBigPit);
			game.getBoard().setSecondPlayerPits(playerPits);
			game.getBoard().setFirstPlayerPits(opponentPits);
			if (!anotherTurn) {
				game.setGameStatus(GameStatus.PLAYER_ONE_TURN);
			}
		}

		return gameRepository.updateGame(game);
	}

	private void makeMoveChecks(Game game, int selectedPit, Player player) {
		if (game.getFinishedAt() != null) {
			throw new IllegalStateException("Game is already finished");
		}
		if (game.getGameStatus() == GameStatus.PENDING_OPPONENT) {
			throw new IllegalStateException("Waiting an opponent");
		}

		if (game.getFirstPlayer().equals(player)) {
			if (GameStatus.PLAYER_ONE_TURN != game.getGameStatus()) {
				throw new IllegalStateException("It is not your turn");
			}
		} else if (game.getSecondPlayer().equals(player)) {
			if (GameStatus.PLAYER_TWO_TURN != game.getGameStatus()) {
				throw new IllegalStateException("It is not your turn");
			}
		} else {
			throw new IllegalArgumentException("Player can not found for this game");
		}

		if (selectedPit < 0 || selectedPit > 6) {
			throw new IllegalArgumentException("Wrong Pit moved.");
		}
	}

	/**
	 * Check if game is over by comparing the pits of both players with an empty
	 * array
	 * 
	 * @return If at least one of the player's all pits are 0 then game is over and
	 *         return true
	 */
	@Override
	public boolean isGameOver(Game game) {
		logger.info("Checking if game is over for game {}", game.getGameId());

		int[] endedPits = new int[] { 0, 0, 0, 0, 0, 0 };

		boolean isGameOver = Arrays.equals(endedPits, game.getBoard().getFirstPlayerPits())
				|| Arrays.equals(endedPits, game.getBoard().getSecondPlayerPits());

		logger.info("is Game Over for game {} is {}", game.getGameId(), isGameOver);
		return isGameOver;
	}

	@Override
	public void calculateWinner(Game game) {
		logger.info("Winner is calculating, {}", game.getGameId());

		int firstPlayerScore = game.getBoard().getFirstPlayerBigPit()
				+ Arrays.stream(game.getBoard().getFirstPlayerPits()).sum();

		int secondPlayerScore = game.getBoard().getSecondPlayerBigPit()
				+ Arrays.stream(game.getBoard().getSecondPlayerPits()).sum();

		int[] endedPits = new int[] { 0, 0, 0, 0, 0, 0 };

		game.getBoard().setFirstPlayerPits(endedPits);
		game.getBoard().setSecondPlayerPits(endedPits);
		game.getBoard().setFirstPlayerBigPit(firstPlayerScore);
		game.getBoard().setSecondPlayerBigPit(secondPlayerScore);

		if (firstPlayerScore > secondPlayerScore) {
			game.setGameStatus(GameStatus.FINISHED_P1_WON);
		} else if (firstPlayerScore < secondPlayerScore) {
			game.setGameStatus(GameStatus.FINISHED_P2_WON);
		} else {
			game.setGameStatus(GameStatus.FINISHED_DRAW);
		}
		logger.info("Result for game {} is {}", game.getGameId(), game.getGameStatus().getDescription());

		game.setFinishedAt(LocalDateTime.now());

		gameRepository.updateGame(game);
	}

	@Override
	public void updateGame(Game game) {
		gameRepository.updateGame(game);
	}

}
