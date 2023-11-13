package numer0n.data;

import java.util.HashMap;

import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

public class Numer0nSocketData {

	/**
	 * socketId, gameId
	 */
	public static HashMap<String, String> gameMap = new HashMap<>();
	/**
	 * socketId, ConcurrentWebSocketSessionDecorator(socket)
	 */
	public static HashMap<String, ConcurrentWebSocketSessionDecorator> socketMap = new HashMap<>();

}
