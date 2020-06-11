package io.github.volkanozkan.mancala.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static io.github.volkanozkan.mancala.model.Constants.INITIAL_STONES_ON_BIGPIT;
import static io.github.volkanozkan.mancala.model.Constants.INITIAL_STONES_ON_PIT;

public class BoardTest {
	private Board board;

	@Before
	public void setup() {
		board = new Board();
	}

	@Test
	public void testCreateBoardCorrectly() {
		Assert.assertNotNull(board);
		Assert.assertEquals(INITIAL_STONES_ON_BIGPIT, board.getFirstPlayerBigPit());
		Assert.assertEquals(INITIAL_STONES_ON_BIGPIT, board.getSecondPlayerBigPit());

		Assert.assertEquals(INITIAL_STONES_ON_PIT * 6, Arrays.stream(board.getFirstPlayerPits()).sum());
		Assert.assertEquals(INITIAL_STONES_ON_PIT * 6, Arrays.stream(board.getSecondPlayerPits()).sum());

	}

	@Test
	public void testBoardsAreEqualAndCorrect() {
		Assert.assertArrayEquals(board.getFirstPlayerPits(), board.getSecondPlayerPits());
		Assert.assertEquals(board.getFirstPlayerBigPit(), board.getSecondPlayerBigPit());

		int[] setup = new int[] { INITIAL_STONES_ON_PIT, INITIAL_STONES_ON_PIT, INITIAL_STONES_ON_PIT,
				INITIAL_STONES_ON_PIT, INITIAL_STONES_ON_PIT, INITIAL_STONES_ON_PIT };

		Assert.assertArrayEquals(setup, board.getSecondPlayerPits());
		Assert.assertArrayEquals(setup, board.getFirstPlayerPits());
	}

	@Test
	public void testSettersWork() {
		board.setFirstPlayerBigPit(1);
		board.setSecondPlayerBigPit(2);
		board.setFirstPlayerPits(new int[] { 0, 1, 0, 3, 0, 0 });
		board.setSecondPlayerPits(new int[] { 1, 1, 1, 1, 1, 1 });

		Assert.assertEquals(1, board.getFirstPlayerBigPit());
		Assert.assertEquals(2, board.getSecondPlayerBigPit());
		Assert.assertEquals(4, Arrays.stream(board.getFirstPlayerPits()).sum());
		Assert.assertEquals(6, Arrays.stream(board.getSecondPlayerPits()).sum());
	}

}
