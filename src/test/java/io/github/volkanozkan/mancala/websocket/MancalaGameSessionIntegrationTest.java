package io.github.volkanozkan.mancala.websocket;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.data.mongodb.database=mancalaTestDB" })
@RunWith(SpringRunner.class)
public class MancalaGameSessionIntegrationTest {
	private final Logger logger = LoggerFactory.getLogger(MancalaGameSessionIntegrationTest.class);

	private FirstPlayerWebSocketClient firstPlayer;
	private SecondPlayerWebSocketClient secondPlayer;
	private WebSocketContainer webSocketContainer;

	private final CountDownLatch messageLatch = new CountDownLatch(6);
	private final CountDownLatch closeSessionLatch = new CountDownLatch(1);

	@Mock
	private DummyMockClass dummyMockClass;

	@Value("${local.server.port}")
	private int port;

	@Before
	public void setUp() {
		webSocketContainer = ContainerProvider.getWebSocketContainer();
		firstPlayer = new FirstPlayerWebSocketClient();
		secondPlayer = new SecondPlayerWebSocketClient();
	}

	/**
	 * This method checks the number of the messages send to sessions. There must be
	 * 7 messages. 2 for creating board and game (both clients will get one message)
	 * 1 move for each player (each player will get 2 messages). 1 message to
	 * opponent when one of the players exit the game.
	 * 
	 * @throws IOException
	 * @throws DeploymentException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testConnectTwoPlayers() throws DeploymentException, IOException, InterruptedException {
		String url = new StringBuilder().append("ws://localhost:").append(port).append("/ws").toString();

		webSocketContainer.connectToServer(firstPlayer, URI.create(url));
		webSocketContainer.connectToServer(secondPlayer, URI.create(url));

		Assert.assertTrue(firstPlayer.session.isOpen());
		Assert.assertTrue(secondPlayer.session.isOpen());

		messageLatch.await(60, TimeUnit.SECONDS);

		firstPlayer.session.close();

		closeSessionLatch.await(5, TimeUnit.SECONDS);

		Assert.assertTrue(secondPlayer.session.isOpen());
		Assert.assertFalse(firstPlayer.session.isOpen());
		verify(dummyMockClass, timeout(1000).times(7)).dummy();
	}

	@ClientEndpoint
	public class FirstPlayerWebSocketClient {
		Session session;

		@OnOpen
		public void onOpen(Session session) throws IOException {
			this.session = session;
			session.getBasicRemote().sendText("{\"selectedPit\":2}");
			logger.debug("First Player moved");
		}

		@OnClose
		public void onClose() {
			logger.debug("First Player session closed");
			closeSessionLatch.countDown();
		}

		@OnMessage
		public void OnMessage(String message) {
			logger.debug("Message Received First Player {}", message);
			dummyMockClass.dummy();
			messageLatch.countDown();
		}
	}

	@ClientEndpoint
	public class SecondPlayerWebSocketClient {
		Session session;

		@OnOpen
		public void onOpen(Session session) throws IOException {
			this.session = session;
			session.getBasicRemote().sendText("{\"selectedPit\":1}");
			logger.debug("Second Player moved");
		}

		@OnClose
		public void onClose() {
			logger.debug("Second Player session closed");
			closeSessionLatch.countDown();
		}

		@OnMessage
		public void OnMessage(String message) {
			logger.debug("Message Received Second Player {}", message);
			dummyMockClass.dummy();
			messageLatch.countDown();
		}

	}

	private static class DummyMockClass {
		void dummy() {
		}
	}

}
