package io.github.volkanozkan.mancala.message;

import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.GameStatus;
import io.github.volkanozkan.mancala.model.Player;

import java.util.Arrays;

/**
 * This class is response template of game for sending to users. It will be
 * serialize by Json
 * 
 * @author volkanozkan
 *
 */
public class GameResponse {

	private String error;
	private int[] myPits;
	private int[] opponentPits;
	private int myBigPit;
	private int opponentBigPit;
	private GameStatus gameStatus;
	private boolean isMyTurn;
	private boolean isGameOver;
	private String myName;
	private String opponentName;

	// Response for init game.
	public GameResponse(Game game, Player player) {
		initGameResponse(game, player);
		this.gameStatus = game.getGameStatus();
		this.isMyTurn = determineIsMyTurn(game, player);
	}

	// Response for sending game to both players after every move.
	public GameResponse(Game game, Player player, boolean isGameOver) {
		initGameResponse(game, player);
		this.gameStatus = game.getGameStatus();
		this.isMyTurn = determineIsMyTurn(game, player);
		this.isGameOver = isGameOver;
	}

	// Response for letting know player his opponent left. Game should over.
	public GameResponse(String error, Game game, Player player) {
		initGameResponse(game, player);
		this.error = error;
		this.gameStatus = game.getGameStatus();
		this.isMyTurn = determineIsMyTurn(game, player);
		this.isGameOver = true;
	}

	// Response for exceptions on move etc. Game can continue.
	public GameResponse(Exception exception, Game game, Player player) {
		initGameResponse(game, player);
		this.error = exception.getMessage();
		this.gameStatus = game.getGameStatus();
		this.isMyTurn = determineIsMyTurn(game, player);
	}

	private void initGameResponse(Game game, Player player) {
		if (player.equals(game.getFirstPlayer())) {
			myPits = game.getBoard().getFirstPlayerPits();
			myBigPit = game.getBoard().getFirstPlayerBigPit();
			opponentBigPit = game.getBoard().getSecondPlayerBigPit();
			opponentPits = game.getBoard().getSecondPlayerPits();
			myName = game.getFirstPlayer().getName();
			opponentName = game.getSecondPlayer() != null ? game.getSecondPlayer().getName()
					: GameStatus.PENDING_OPPONENT.getDescription();
		} else if (player.equals(game.getSecondPlayer())) {
			opponentPits = game.getBoard().getFirstPlayerPits();
			opponentBigPit = game.getBoard().getFirstPlayerBigPit();
			myBigPit = game.getBoard().getSecondPlayerBigPit();
			myPits = game.getBoard().getSecondPlayerPits();
			opponentName = game.getFirstPlayer().getName();
			myName = game.getSecondPlayer().getName();
		}
	}

	private boolean determineIsMyTurn(Game game, Player player) {
		if (game.getFirstPlayer().equals(player)) {
			return GameStatus.PLAYER_ONE_TURN.equals(game.getGameStatus());
		} else if (game.getSecondPlayer().equals(player)) {
			return GameStatus.PLAYER_TWO_TURN.equals(game.getGameStatus());
		}
		return false;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int[] getMyPits() {
		return myPits;
	}

	public void setMyPits(int[] myPits) {
		this.myPits = myPits;
	}

	public int[] getOpponentPits() {
		return opponentPits;
	}

	public void setOpponentPits(int[] opponentPits) {
		this.opponentPits = opponentPits;
	}

	public int getMyBigPit() {
		return myBigPit;
	}

	public void setMyBigPit(int myBigPit) {
		this.myBigPit = myBigPit;
	}

	public int getOpponentBigPit() {
		return opponentBigPit;
	}

	public void setOpponentBigPit(int opponentBigPit) {
		this.opponentBigPit = opponentBigPit;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}

	@Override
	public String toString() {
		return "GameResponse [error=" + error + ", myPits=" + Arrays.toString(myPits) + ", opponentPits="
				+ Arrays.toString(opponentPits) + ", myBigPit=" + myBigPit + ", opponentBigPit=" + opponentBigPit
				+ ", gameStatus=" + gameStatus + ", isMyTurn=" + isMyTurn + ", isGameOver=" + isGameOver + ", myName="
				+ myName + ", opponentName=" + opponentName + "]";
	}

}
