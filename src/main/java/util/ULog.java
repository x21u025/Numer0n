package util;

import java.util.StringJoiner;

import dao.AccessLogDao;
import dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class ULog {

	public static void accessPage(HttpServletRequest request) {
		accessPage(request, null);
	}
	public static void accessPage(HttpServletRequest request, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		accessPage(request, session != null ? session.getId() : null, userId, message);
	}
	private static void accessPage(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "ACCESS_PAGE", sessionId, userId, message);
	}

	public static void login(HttpServletRequest request) {
		login(request, null);
	}
	public static void login(HttpServletRequest request, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		login(request, session != null ? session.getId() : null, userId, message);
	}
	private static void login(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "LOGIN", sessionId, userId, message);
	}

	public static void register(HttpServletRequest request) {
		register(request, null);
	}
	public static void register(HttpServletRequest request, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		register(request, session != null ? session.getId() : null, userId, message);
	}
	private static void register(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "REGISTER", sessionId, userId, message);
	}

	public static void logout(HttpServletRequest request) {
		logout(request, null);
	}
	public static void logout(HttpServletRequest request, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		logout(request, session != null ? session.getId() : null, userId, message);
	}
	private static void logout(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "LOGOUT", sessionId, userId, message);
	}

	public static void notPermission(HttpServletRequest request) {
		notPermission(request, null);
	}
	public static void notPermission(HttpServletRequest request, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		notPermission(request, session != null ? session.getId() : null, userId, message);
	}
	private static void notPermission(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "NOT_PERMISSION", sessionId, userId, message);
	}

	public static void startGame(HttpServletRequest request, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		startGame(request, session != null ? session.getId() : null, userId, message);
	}
	private static void startGame(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "START_GAME", sessionId, userId, message);
	}

	public static void webSocketConnect(String sessionId, User user, String page, String message) {
		String userId = null;
		if(user != null) userId = user.getId();
		webSocketConnect(sessionId, userId, page, message);
	}
	private static void webSocketConnect(String sessionId, String userId, String page, String message) {
		webSocket("CONNECT", sessionId, userId, page, message);
	}

	public static void webSocketDisconnect(String sessionId, User user, String page, String message) {
		String userId = null;
		if(user != null) userId = user.getId();
		webSocketDisconnect(sessionId, userId, page, message);
	}
	private static void webSocketDisconnect(String sessionId, String userId, String page, String message) {
		webSocket("DISCONNECT", sessionId, userId, page, message);
	}

	public static void webSocketOnMessage(String sessionId, User user, String page, String message) {
		String userId = null;
		if(user != null) userId = user.getId();
		webSocketOnMessage(sessionId, userId, page, message);
	}
	private static void webSocketOnMessage(String sessionId, String userId, String page, String message) {
		webSocket("ON_MESSAGE", sessionId, userId, page, message);
	}

	public static void webSocketOnError(String sessionId, User user, String page, Throwable e) {
		String userId = null;
		if(user != null) userId = user.getId();

		StringJoiner sj = new StringJoiner("\n");
		for(StackTraceElement s : e.getStackTrace()) {
			sj.add(s.toString());
		}
		webSocketOnError(sessionId, userId, page, sj.toString());
	}
	private static void webSocketOnError(String sessionId, String userId, String page, String message) {
		webSocket("ON_ERROR", sessionId, userId, page, message);
	}

	public static void webSocketSendMessage(String sessionId, User user, String page, String message) {
		String userId = null;
		if(user != null) userId = user.getId();
		webSocketSendMessage(sessionId, userId, page, message);
	}
	private static void webSocketSendMessage(String sessionId, String userId, String page, String message) {
		webSocket("SEND_MESSAGE", sessionId, userId, page, message);
	}

	private static void webSocket(String process, String sessionId, String userId, String page, String message) {
		AccessLogDao.insertLog(process, null, sessionId, userId, page, message);
	}

	public static void error(HttpServletRequest request, Exception e) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}

		StringJoiner sj = new StringJoiner("\n");
		sj.add(e.getStackTrace()[0].toString());
		sj.add(e.getMessage());

		error(request, session != null ? session.getId() : null, userId, sj.toString());
	}
	private static void error(HttpServletRequest request, String sessionId, String userId, String message) {
		insert(request, "ERROR", sessionId, userId, message);
	}

	public static void insert(HttpServletRequest request, String process) {
		insert(request, process, null);
	}
	public static void insert(HttpServletRequest request, String process, String message) {
		HttpSession session = request.getSession(false);
		String userId = null;
		if(session != null) {
			User user = (User) session.getAttribute("user");
			if(user != null) userId = user.getId();
		}
		insert(request, process, session != null ? session.getId() : null, userId, message);
	}
	private static void insert(HttpServletRequest request, String process, String sessionId, String userId, String message) {
		String finalUrl = (request.getQueryString() != null) ? String.join("", request.getRequestURI(),"?" ,request.getQueryString()) : request.getRequestURI().toString();
		AccessLogDao.insertLog(process, request.getHeader("X-Forwarded-For"), sessionId, userId, finalUrl, message);
	}

}
