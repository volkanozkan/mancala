package com.vozkan.mancala.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Game Class which holds all informations of game.
 * 
 * @author volkanozkan
 *
 */
@Document("games")
public class Game {

	@Id
	@Indexed
	private UUID gameId;
	private Board board;
	private Player firstPlayer;
	private Player secondPlayer;
	private GameStatus gameStatus;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime startedAt;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime finishedAt;

	public Game() {
		this.gameId = UUID.randomUUID();
		this.board = new Board();
		this.gameStatus = GameStatus.PENDING_OPPONENT;
		this.startedAt = LocalDateTime.now();
	}

	public UUID getGameId() {
		return gameId;
	}

	public Board getBoard() {
		return board;
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public void setFirstPlayer(Player firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public void setSecondPlayer(Player secondPlayer) {
		this.secondPlayer = secondPlayer;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	@Override
	public String toString() {
		return "Game [gameId=" + gameId + ", board=" + board + ", firstPlayer=" + firstPlayer + ", secondPlayer="
				+ secondPlayer + ", gameStatus=" + gameStatus + ", startedAt=" + startedAt + ", finishedAt="
				+ finishedAt + "]";
	}

}
