package util;

import java.util.HashMap;

import dao.UserDao;
import dto.User;

public class Util {

	/**
	 * SessionID, userId
	 */
	private static HashMap<String, String> sessions = new HashMap<>();

	public static boolean checkPassword(String pass) {
		return pass != null && !pass.trim().equals("") && pass.length() >= 8 && pass.length() <= 32;
	}

	public static boolean checkId(String id) {
		return id != null && !id.trim().equals("") && id.length() >= 4 && id.length() <= 32;
	}

	public static boolean checkName(String name) {
		return name != null && !name.trim().equals("") && name.length() >= 1 && name.length() <= 32;
	}

	public static boolean isLogin(String sessionId) {
		return sessions.get(sessionId) != null;
	}

	public static void setSession(String sessionId, String userId) {
		sessions.put(sessionId, userId);
	}

	public static void removeSession(String sessionId) {
		sessions.remove(sessionId);
	}

	public static User getUserBySession(String sessionId) {
		String userId = sessions.get(sessionId);
		if(userId == null) return null;
		return new UserDao().findUserById(userId);
	}

	public static boolean intOrEquals(int i, int... n) {
		for(int a = 0; a < n.length; a++) {
			if(n[a] == i) return true;
		}

		return false;
	}

}
