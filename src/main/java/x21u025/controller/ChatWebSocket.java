package x21u025.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import dao.ChatLogDao;
import dao.UserDao;
import dto.ChatLog;
import dto.User;
import model.chat.Json;
import util.Util;

public class ChatWebSocket extends TextWebSocketHandler {

	public static ArrayList<WebSocketSession> userList = new ArrayList<WebSocketSession>();
	private static Gson gson = new Gson();
	private static ChatLogDao chatLogDao = new ChatLogDao();
	private static UserDao userDao = new UserDao();

	private String sessionId;
	private User user;


	/**
	 * メッセージの送受信
	 * @throws IOException
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage _message) throws IOException {
		String message = _message.getPayload();
		System.out.println(message);
		Json json = gson.fromJson(message, Json.class);
		json.user.name = userDao.findNameById(json.user.id);
		chatLogDao.insertLog(json.user.id, json.message);
		TextMessage tm = new TextMessage(gson.toJson(json));
		for(WebSocketSession s : userList) {
			s.sendMessage(tm);
		}
	}

	/**
	 * 接続確立
	 */
	@Override
 	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.sessionId = (String) session.getAttributes().get("sessionId");
		this.user = Util.getUserBySession(sessionId);
		if(this.user != null) {
			userList.add(session);

			Json json = new Json();
			json.user.id = user.getId();
			json.user.name = user.getName();
			json.type = "USER";
			session.sendMessage(new TextMessage(gson.toJson(json)));

			for(ChatLog cl : chatLogDao.getLogs(10)) {
				session.sendMessage(new TextMessage(gson.toJson(toJson(cl))));
			}
			sendChatCount();
			QueueWebSocket.sendQueueCount();
		}
}

	/**
	 * 接続終了
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		userList.remove(session);
		sendChatCount();
	}

	private Json toJson(ChatLog chatLog) {
		Json json = new Json();
		json.user.id = chatLog.getPlayer();
		json.user.name = userDao.findNameById(chatLog.getPlayer());
		json.message = chatLog.getMessage();
		json.type = "CHAT";
		return json;
	}

	private void sendChatCount() {
		Json chatCount = new Json();
		chatCount.type = "CHAT_COUNT";
		chatCount.message = "" + userList.size();
		TextMessage tm = new TextMessage(gson.toJson(chatCount));
		for(WebSocketSession s : userList) {
			try {
				s.sendMessage(tm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
