package com.vozkan.mancala.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * The Board of the game. Board contain all the pits.
 * 
 * @author volkanozkan
 *
 */
public class Board {
	private static final Logger logger = LoggerFactory.getLogger(Board.class);

	private int[] firstPlayerPits;
	private int[] secondPlayerPits;
	private int firstPlayerBigPit;
	private int secondPlayerBigPit;

	/**
	 * Initialize the board
	 * 
	 */
	public Board() {
		logger.debug("Pits are creating on board.");
		firstPlayerPits = new int[] { Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT,
				Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT };
		firstPlayerBigPit = Constants.INITIAL_STONES_ON_BIGPIT;

		secondPlayerPits = new int[] { Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT,
				Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT, Constants.INITIAL_STONES_ON_PIT };
		secondPlayerBigPit = Constants.INITIAL_STONES_ON_BIGPIT;

		logger.debug("Pits created");
	}

	public int[] getFirstPlayerPits() {
		return firstPlayerPits;
	}

	public void setFirstPlayerPits(int[] firstPlayerPits) {
		this.firstPlayerPits = firstPlayerPits;
	}

	public int[] getSecondPlayerPits() {
		return secondPlayerPits;
	}

	public void setSecondPlayerPits(int[] secondlayerPits) {
		this.secondPlayerPits = secondlayerPits;
	}

	public int getFirstPlayerBigPit() {
		return firstPlayerBigPit;
	}

	public void setFirstPlayerBigPit(int firstPlayerBigPit) {
		this.firstPlayerBigPit = firstPlayerBigPit;
	}

	public int getSecondPlayerBigPit() {
		return secondPlayerBigPit;
	}

	public void setSecondPlayerBigPit(int secondPlayerBigPit) {
		this.secondPlayerBigPit = secondPlayerBigPit;
	}

	@Override
	public String toString() {
		return "Board [firstPlayerPits=" + Arrays.toString(firstPlayerPits) + ", secondPlayerPits="
				+ Arrays.toString(secondPlayerPits) + ", firstPlayerBigPit=" + firstPlayerBigPit
				+ ", secondPlayerBigPit=" + secondPlayerBigPit + "]";
	}

}
