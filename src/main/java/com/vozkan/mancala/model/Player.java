package com.vozkan.mancala.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Represent the player of the game.
 * 
 * @author volkanozkan
 *
 */
@Document("players")
public class Player {

	@Id
	@Indexed
	private String sessionId;
	private String name;
	private UUID gameId;

	public Player(String name, String sessionId, UUID gameId) {
		this.name = name;
		this.sessionId = sessionId;
		this.gameId = gameId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionID) {
		this.sessionId = sessionID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameId == null) ? 0 : gameId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (gameId == null) {
			if (other.gameId != null)
				return false;
		} else if (!gameId.equals(other.gameId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Player [sessionId=" + sessionId + ", name=" + name + ", gameId=" + gameId + "]";
	}

}
