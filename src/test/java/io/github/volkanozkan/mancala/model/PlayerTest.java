package io.github.volkanozkan.mancala.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerTest {

	@Mock
	private WebSocketSession webSocketSession;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreatePlayer() {
		when(webSocketSession.getId()).thenReturn("1");
		Player player = new Player("Test User", webSocketSession.getId(), UUID.randomUUID());
		Assert.assertNotNull(player);
		Assert.assertNotNull(player.getName());
		Assert.assertNotNull(player.getGameId());
		Assert.assertEquals("Test User", player.getName());
		Assert.assertNotNull(player.getSessionId());
	}

	@Test
	public void testPlayerEqualsAndHashCode() {
		UUID uuid = UUID.randomUUID();
		Player player = new Player("Test User1", webSocketSession.getId(), uuid);
		Player player2 = new Player("Test User1", webSocketSession.getId(), uuid);
		Player player3 = new Player("Test User2", webSocketSession.getId(), uuid);
		Player player4 = new Player("Test User2", webSocketSession.getId(), UUID.randomUUID());

		Assert.assertTrue(player.equals(player2) && player2.equals(player));
		Assert.assertEquals(player.hashCode(), player2.hashCode());
		Assert.assertNotEquals(player3.hashCode(), player2.hashCode());
		Assert.assertFalse(player.equals(player3) && player3.equals(player));
		Assert.assertFalse(player4.equals(player3) && player3.equals(player4));
	}

}
