package x21u025.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.GsonBuilder;

import dao.GameLogDao;
import dao.UserDao;
import dto.GameLog;
import dto.User;
import model.json.Json;
import numer0n.HitAndBlow;
import util.ULog;
import util.Util;

public class WatchWebSocket extends TextWebSocketHandler  {

	// String is GameID
	// ArrayList<WebSocketSession> is Watcher
	static HashMap<String, ArrayList<WatchWebSocket>> watchers = new HashMap<String, ArrayList<WatchWebSocket>>();

	private WebSocketSession session;
	private String gameId;
	private String sessionId;
	// String is PlayerID
	// String is Number
	private HashMap<String, String> numbers = new HashMap<String, String>();

	public static void sendMessage(String gameId) {
		ArrayList<GameLog> list = new GameLogDao().getLogs(gameId);
		GameLog lastLog = list.get(list.size() - 1);

		ArrayList<WatchWebSocket> w = watchers.get(gameId);
		if(w != null) {
			for(WatchWebSocket ws : w) {
				try {
					ws.session.sendMessage(new TextMessage("[" + ws.toJson(lastLog) + "]"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * メッセージの送受信
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {}


	/**
	 * 接続確立
	 */
	@Override
 	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.gameId = (String) session.getAttributes().get("gameId");
		this.sessionId = (String) session.getAttributes().get("sessionId");
		User user = Util.getUserBySession(sessionId);
		System.out.printf("[Watch] onOpen: %s(%s): %s\n", user.getName(), user.getId(), session.getId());
		ULog.webSocketConnect("[WATCH] " + session.getId(), user, "/ws/Watch?gameId=" + gameId + "&sessionId=" + sessionId, gameId);

		ArrayList<WatchWebSocket> list = watchers.get(gameId);
		if(list == null) list = new ArrayList<WatchWebSocket>();
		list.add(this);
		this.session = session;
		watchers.put(gameId, list);

		StringJoiner sj = new StringJoiner(",");
		for(GameLog gl : new GameLogDao().getLogs(gameId)) {
			sj.add(toJson(gl));
		}
		session.sendMessage(new TextMessage("[" + sj + "]"));	// アクセスする前にあるデータをJsonで
	}

	/**
	 * 接続終了
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		User user = Util.getUserBySession(sessionId);
		System.out.printf("[Watch] onClose: %s(%s): %s\n", user.getName(), user.getId(), session.getId());
		ULog.webSocketDisconnect("[WATCH] " + session.getId(), user, "/ws/Watch?gameId=" + gameId + "&sessionId=" + sessionId, gameId);

		ArrayList<WatchWebSocket> list = watchers.get(gameId);
		list.remove(this);
		watchers.put(gameId, list);
	}


	private String toJson(GameLog gamelog) {
		ArrayList<User> users = new UserDao().getAllUser();
		Json json = new Json();
		if(!(gamelog.getProcess().equals("CREATE") || gamelog.getProcess().equals("FIN"))) {
			User thisUser = users.stream().filter(u -> u.getId().equals(gamelog.getPlayer())).findFirst().get();
			json.user.id = thisUser.getId();
			json.user.name = thisUser.getName();
			if(gamelog.getProcess().equals("SET_NUMBER")) {
				numbers.put(thisUser.getId(), gamelog.getNumber());
			}
			if(gamelog.getProcess().equals("ANSWER_NUMBER")) {
				String enemyId = numbers.keySet().stream().filter(p -> !p.equals(thisUser.getId())).findFirst().get();
				HitAndBlow hab = HitAndBlow.createHitAndBlow(numbers.get(enemyId), gamelog.getNumber());
				json.hab.hit = hab.getHit();
				json.hab.blow = hab.getBlow();
			}
			json.number = gamelog.getNumber();
		}
		json.message = gamelog.getProcess();
		return new GsonBuilder().serializeNulls().create().toJson(json);
	}
}