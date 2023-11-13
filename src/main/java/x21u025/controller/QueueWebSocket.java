package x21u025.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import dto.User;
import model.chat.Json;
import numer0n.Numer0n;
import util.ULog;

public class QueueWebSocket extends TextWebSocketHandler {

	// WebSocketSession is Session
	// String is Hash
	static LinkedHashMap<WebSocketSession, String> queue = new LinkedHashMap<>();
	// String is Hash
	// User is User
	public static HashMap<String, User> hashToUser = new HashMap<>();

	private static QueueThread thread = new QueueThread();

	private String hash;

	/**
	 * メッセージの送受信
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		if(message.getPayload().equals("queue")) {
			for(WebSocketSession q : queue.keySet()) {
				try {
					q.sendMessage(new TextMessage(queue.size() + ""));
				} catch (Exception e) {
				}
			}
		}
	}


	/**
	 * 接続確立
	 */
	@Override
 	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.hash = (String) session.getAttributes().get("hash");
		User user = hashToUser.get(hash);
		System.out.printf("[Queue] onOpen: %s(%s): %s\n", user.getName(), user.getId(), session.getId());
		ULog.webSocketConnect("[QUEUE] " + session.getId(), user, "/ws/Queue?hash=" + hash, (queue.size() + 1) + "人待ち");
		queue.put(session, hash);
		queue.keySet().stream().forEach(q -> {
			try {
				q.sendMessage(new TextMessage(queue.size() + ""));
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		});
		if(queue.size() % 2 == 0 && queue.size() != 0) {
			thread.run();
			ULog.webSocketOnMessage("[QUEUE] " + session.getId(), user, "/ws/Queue", "CAN I PLAY THE GAME");
		}

		sendQueueCount();

	}

	/**
	 * 接続終了
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		try {
			User user = hashToUser.get(queue.get(session));
			System.out.printf("[Queue] onClose: %s(%s): %s\n", user.getName(), user.getId(), session.getId());
			ULog.webSocketDisconnect("[QUEUE] " + session.getId(), user, "/ws/Queue", (queue.size() - 1) + "人待ち");
			queue.remove(session);
			queue.keySet().stream().forEach(q -> {
				try {
					q.sendMessage(new TextMessage(queue.size() + ""));
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			});
			sendQueueCount();
		} catch (Exception e) {}
	}


	public static void sendQueueCount() {
		Json queueCount = new Json();
		queueCount.type = "QUEUE_COUNT";
		queueCount.message = "" + queue.size();
		TextMessage tm = new TextMessage(new Gson().toJson(queueCount));
		for(WebSocketSession s : ChatWebSocket.userList) {
			try {
				s.sendMessage(tm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class QueueThread {
	synchronized public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		var queue = QueueWebSocket.queue;
		var hashToUser = QueueWebSocket.hashToUser;
		if(queue.size() >= 2) {
			int i = 0;
			WebSocketSession player1 = null;
			WebSocketSession player2 = null;
			for(Entry<WebSocketSession, String> s : queue.entrySet()) {
				if(player1 == null) {
					player1 = s.getKey();
				} else {
					player2 = s.getKey();
				}

				if(++i == 2) {
					Numer0n numer0n = new Numer0n(hashToUser.get(queue.get(player1)), hashToUser.get(queue.get(player2)));
//					numer0n.start(); //二人のプレイヤーが接続したら実行に変更
					try {
						player1.sendMessage(new TextMessage("start"));
						player2.sendMessage(new TextMessage("start"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					User user1 = numer0n.getPlayer1().getPlayer();
					User user2 = numer0n.getPlayer2().getPlayer();
					System.out.printf("[Queue] onClose: %s(%s): %s\n", user1.getName(), user1.getId(), player1.getId());
					System.out.printf("[Queue] onClose: %s(%s): %s\n", user2.getName(), user2.getId(), player2.getId());
					ULog.webSocketDisconnect("[QUEUE] " + player1.getId(), user1, "/QueueWebSocket", (queue.size() - 2) + "人待ち");
					ULog.webSocketDisconnect("[QUEUE] " + player2.getId(), user2, "/QueueWebSocket", (queue.size() - 2) + "人待ち");
					QueueWebSocket.queue.remove(player1);
					QueueWebSocket.queue.remove(player2);
					return;
				}
			}
		}
	}
}
