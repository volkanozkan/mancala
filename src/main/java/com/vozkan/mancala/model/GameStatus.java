package com.vozkan.mancala.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Identify the Game Status and it's description
 * 
 * @JsonFormat added because accessing to description field
 * @author volkanozkan
 * 
 * @see JsonFormat
 *
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameStatus {
	PENDING_OPPONENT("Pending Opponent"), OPPONENT_LEFT("Opponent Left"), FINISHED("Game Finished"),
	PLAYER_ONE_TURN("Player One Turn"), PLAYER_TWO_TURN("Player Two Turn"), FINISHED_DRAW("Game Finished as Draw"),
	FINISHED_P1_WON("Game Finished, Player 1 Won"), FINISHED_P2_WON("Game Finished, Player 2 Won");

	GameStatus(String description) {
		this.description = description;
	}

	private final String description;

	public String getDescription() {
		return description;
	}

}
