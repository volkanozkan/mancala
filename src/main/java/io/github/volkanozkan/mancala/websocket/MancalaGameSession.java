package io.github.volkanozkan.mancala.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.volkanozkan.mancala.message.GameResponse;
import io.github.volkanozkan.mancala.model.Constants;
import io.github.volkanozkan.mancala.model.Game;
import io.github.volkanozkan.mancala.model.GameStatus;
import io.github.volkanozkan.mancala.model.Player;
import io.github.volkanozkan.mancala.service.GameService;
import io.github.volkanozkan.mancala.service.PlayerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for communication between websocket and server
 * 
 * @author volkanozkan
 * 
 */
public class MancalaGameSession extends TextWebSocketHandler {
	private final Logger logger = LoggerFactory.getLogger(MancalaGameSession.class);

	// Store webSocketSessions as thread-safe with ConcurrentHashMap. Later use this
	// map to find session from a player for sending message.
	private final Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();

	@Autowired
	private GameService gameService;
	@Autowired
	private PlayerService playerService;

	/**
	 * When websocket connection is sent from client side. New player fill join an
	 * existing game if there is any available game, if not it will be the first
	 * player of newly created game. Lastly, game initialization will send to
	 * player/players of the game.
	 * 
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		webSocketSessions.put(webSocketSession.getId(), webSocketSession);

		String playerName = webSocketSession.getUri().getQuery();
		if (playerName == null || "".equals(playerName) || "null".equals(playerName)) {
			playerName = Constants.UNKNOWN_PLAYER;
		}
		logger.info("Connection established sessionId = {}, username = {}", webSocketSession.getId(), playerName);

		try {
			synchronized (this) {
				Game game = gameService.createOrFindAvailableGame();
				Player player = playerService.createNewPlayer(playerName, webSocketSession.getId(), game.getGameId());

				if (game.getFirstPlayer() != null) {
					logger.info("player joined to existing game");
					gameService.joinToExistingGame(game, player);

					// send game to first player(already waiting player) as they can start a game
					sendGameResponse(findWebSocketSession(game.getFirstPlayer()),
							new GameResponse(game, game.getFirstPlayer()));
				} else {
					logger.info("created new game");
					gameService.joinNewGameAsFirstPlayer(game, player);
				}
				// send game to connected player
				sendGameResponse(webSocketSession, new GameResponse(game, player));
			}
		} catch (Exception e) {
			logger.info("Exception on afterConnectionEstablished {}, {}", e.getMessage(), e.getStackTrace());
			webSocketSession.close();
		}
	}

	/**
	 * When the connection is closed from any player. Terminate the game and if
	 * there is an already playing opponent, let player know game is finished
	 * 
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) {
		logger.info("Connection closed sessionId = {}", webSocketSession.getId());

		final Player player = playerService.getPlayerBySessionId(webSocketSession.getId());

		gameService.terminateGame(player.getGameId());

		final Player opponent = playerService.getPlayersOpponent(player);
		if (opponent != null) {
			logger.debug("Letting know that opponent left to {}", opponent);
			Game game = gameService.getGameById(player.getGameId());
			WebSocketSession opponentSession = findWebSocketSession(opponent);
			sendGameResponse(opponentSession,
					new GameResponse(GameStatus.OPPONENT_LEFT.getDescription(), game, opponent));
		}

		logger.info("Game {} terminated", player.getGameId());
	}

	/**
	 * Receive message from client side. Make move and check if game should over or
	 * continue. Lastly, new board/game status will send to players of the game to
	 * update their screen.
	 * 
	 */
	@Override
	protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) {
		logger.info("Message received from sessionId: {}, message: {}", webSocketSession.getId(), message.getPayload());
		Player player = null;

		try {
			JSONObject jsonObj = new JSONObject(message.getPayload());
			int selectedPit = jsonObj.getInt("selectedPit");

			// find player who is playing turn
			player = playerService.getPlayerBySessionId(webSocketSession.getId());

			// make move for selected pit
			Game game = gameService.move(player.getGameId(), selectedPit, player);

			// check if game is over and determine the winner or draw status.
			// The game is over as soon as one of the sides runs out of stones. The player
			// who still has stones in his pits keeps them and puts them in his big pit. The
			// winner of the game is the player who has the most stones in his big pit.
			boolean isGameOver = gameService.isGameOver(game);
			if (isGameOver) {
				gameService.calculateWinner(game);
			}
			// send game to first player
			sendGameResponse(findWebSocketSession(game.getFirstPlayer()),
					new GameResponse(game, game.getFirstPlayer(), isGameOver));

			// send game to second player
			sendGameResponse(findWebSocketSession(game.getSecondPlayer()),
					new GameResponse(game, game.getSecondPlayer(), isGameOver));

		} catch (Exception exception) {
			logger.error("Error on move.. {}, {}", exception.getMessage(), exception.getStackTrace());

			if (player != null) {
				Game game = gameService.getGameById(player.getGameId());
				sendGameResponse(webSocketSession, new GameResponse(exception, game, player));
			} else {
				throw exception;
			}
		}

	}

	/**
	 * Serialize the gameResponse and send to the player's session
	 * 
	 * @param session
	 * @param gameResponse
	 */
	private void sendGameResponse(WebSocketSession webSocketSession, GameResponse gameResponse) {
		try {
			if (webSocketSession != null && webSocketSession.isOpen()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				synchronized (this) {
					webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(gameResponse)));
				}
				logger.info("Game Response sent to {} as {}", webSocketSession.getId(), gameResponse);
			}
		} catch (Exception exception) {
			logger.info("Failed on sending game response {}", exception.getMessage());
			throw new IllegalStateException(exception);
		}
	}

	private WebSocketSession findWebSocketSession(Player player) {
		return webSocketSessions.get(player.getSessionId());
	}
}
