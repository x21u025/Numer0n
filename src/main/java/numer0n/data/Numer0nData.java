package numer0n.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import numer0n.Numer0n;

public class Numer0nData {

	/**
	 * GameID, Numer0n
	 */
	private static HashMap<String, Numer0n> gameMap = new HashMap<String, Numer0n>();

	public static void put(String gameId, Numer0n numer0n) {
		gameMap.put(gameId, numer0n);
	}

	public static Numer0n get(String gameId) {
		return gameMap.get(gameId);
	}

	public static void remove(String gameId) {
		gameMap.remove(gameId);
	}

	public static Set<Entry<String, Numer0n>> getAllGame() {
		return gameMap.entrySet();
	}

}
