package x21u025.controller;

import static numer0n.data.Numer0nSocketData.*;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dao.LeaderBoardDao;
import dao.RecordDao;
import dto.User;
import exception.Numer0nException;
import model.json.Json;
import numer0n.HitAndBlow;
import numer0n.Numer0n;
import numer0n.Numer0n.Phase;
import numer0n.Numer0nPlayer;
import numer0n.data.Numer0nData;
import util.ULog;

public class GameWebSocket extends TextWebSocketHandler {

	private String gameId;
	private String sessionId;

	/**
	 * メッセージの送受信
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage _message) {
		// ゲームの進行をここで
		// Numer0nと合わせて処理
		// methodも使いながら処理をわかりやすく
		String message = _message.getPayload();
		System.out.println(message);
		Json reciveJson = new Gson().fromJson(message, Json.class);
		try {
			String gameId = gameMap.get(session.getId());
			Numer0n numer0n = Numer0nData.get(gameId);
			Numer0nPlayer player = numer0n.getPlayerBySocket(session.getId());
			Numer0nPlayer otherPlayer = numer0n.getPlayerInt(player.getPlayer().getId()) == 1 ? numer0n.getPlayer2() : numer0n.getPlayer1();
			User user = numer0n.getPlayerBySocket(session.getId()).getPlayer();
			System.out.printf("[Game] onMessage: %s(%s): %s: \n", user.getName(), user.getId(), session.getId(), message);
			ULog.webSocketOnMessage("[GAME] " + session.getId(), user, "/GameWebSocket?gameId=" + gameId, message);

			Json json = null;
			System.out.println("[Before] " + numer0n.getPhase());
			ULog.webSocketOnMessage("[BEFORE_PHASE] " + session.getId(), user, "/GameWebSocket?gameId=" + gameId, numer0n.getPhase().toString());
			if(reciveJson.message.equals("Surrender")) {
				json = new Json();
				json.user.id = player.getPlayer().getId();
				json.message = "Surrender";
				sendThisGamePlayer(gameId, json);
				SurrenderScore(numer0n, otherPlayer.getPlayer().getId(), player.getPlayer().getId());
				numer0n.getDao().insertLog("Surrender", player.getPlayer().getId(), null);
				numer0n.surrender();
				return;
			}
			if(reciveJson.message.equals("Message")) {
				json = new Json();
				json.user.id = player.getPlayer().getId();
				json.message = "Message";
				json.number = reciveJson.number;
				sendThisGamePlayer(gameId, json);
				numer0n.getDao().insertLog("MESSAGE", player.getPlayer().getId(), reciveJson.number);
				return;
			}
			switch(numer0n.getPhase()) {
				case INIT: //0
					break;
				case WAIT_SET_PLYAER: //1
				case SET_PLAYER_1: //2
				case SET_PLAYER_2: //3
					if(numer0n.getPhase() == Phase.SET_PLAYER_1 && numer0n.getPlayerInt(player.getPlayer().getId()) == 1) {
						// 同一プレイヤーによる実行(書き換えようとしている)
						// 何も処理しない予定
						// player1
					} else if(numer0n.getPhase() == Phase.SET_PLAYER_2 && numer0n.getPlayerInt(player.getPlayer().getId()) == 2) {
						// 同一プレイヤーによる実行(書き換えようとしている)
						// 何も処理しない予定
						// player2
					} else {
						numer0n.getDao().insertLog("SET_NUMBER", player.getPlayer().getId(), reciveJson.number);


						if(numer0n.setNumber(reciveJson.number, numer0n.getPlayerInt(player.getPlayer().getId()))) {
							json = new Json();
							json.message = "startNumer0n";
							json.number = numer0n.getPlayer1().getPlayer().getId() + "|" + numer0n.getPlayer2().getPlayer().getId();
							numer0n.next(); // 4 -> 5
							numer0n.next(); // 5 -> 6
						}

						Json sendJson = new Json();
						sendJson.user.id = player.getPlayer().getId();
						sendJson.number = player.getNumber();
						sendJson.message = "setNumber";

						sendThisGamePlayer(gameId, sendJson);

					}
					break;
				case COLLECT_SET_PLAYER: //4
				case START_GAME: //5
					break;
				case WAIT_SEND_NUM_PLAYER_1: //6
					if(numer0n.getPlayerInt(player.getPlayer().getId()) == 1) {
						numer0n.getDao().insertLog("ANSWER_NUMBER", player.getPlayer().getId(), reciveJson.number);
						json = getHitAndBlow(reciveJson.number, numer0n, player);
						numer0n.next(); // 7 -> 8
					}
					break;
				case COLLECT_NUM_PLAYER_1: //7
					break;
				case WAIT_SEND_NUM_PLAYER_2: //8
					if(numer0n.getPlayerInt(player.getPlayer().getId()) == 2) {
						numer0n.getDao().insertLog("ANSWER_NUMBER", player.getPlayer().getId(), reciveJson.number);
						json = getHitAndBlow(reciveJson.number, numer0n, player);
						numer0n.next(); // 9 -> 10
						// 二人が入力を終えた
						numer0n.checkFin();
						// 当たってたら
						if(numer0n.getPhase().getNum() >= 11 && numer0n.getPhase().getNum() <= 13) {
							Json send1Json = new Json();
							Json send2Json = new Json();

							if(numer0n.getPhase() == Phase.ALL_HIT_PLAYER_1) {
								numer0n.getDao().insertLog("WIN_PLAYER_1", numer0n.getPlayer1().getPlayer().getId(), null);
								WatchWebSocket.sendMessage(gameId);
								send1Json.hab.hit = 1;
								send2Json.hab.hit = 1;

								winLoseScore(numer0n, numer0n.getPlayer1().getPlayer().getId(), numer0n.getPlayer2().getPlayer().getId());
							} else if(numer0n.getPhase() == Phase.ALL_HIT_PLAYER_2){
								numer0n.getDao().insertLog("WIN_PLAYER_2", numer0n.getPlayer2().getPlayer().getId(), null);
								WatchWebSocket.sendMessage(gameId);
								send1Json.hab.hit = 2;
								send2Json.hab.hit = 2;

								winLoseScore(numer0n, numer0n.getPlayer2().getPlayer().getId(), numer0n.getPlayer1().getPlayer().getId());
							} else {
								numer0n.getDao().insertLog("DRAW", null, null);
								WatchWebSocket.sendMessage(gameId);
								send1Json.hab.hit = 3;
								send2Json.hab.hit = 3;

								drawScore(numer0n, numer0n.getPlayer1().getPlayer().getId(), numer0n.getPlayer2().getPlayer().getId());
							}

							sendThisGamePlayer(gameId, json);
							// リザルトへ
							json = null;

							send1Json.user.id = numer0n.getPlayer2().getPlayer().getId();
							send1Json.number = numer0n.getPlayer2().getNumber();
							send1Json.message = "hitNumber";
							send2Json.user.id = numer0n.getPlayer1().getPlayer().getId();
							send2Json.number = numer0n.getPlayer1().getNumber();
							send2Json.message = "hitNumber";

							send(socketMap.get(numer0n.getPlayer1().getSocketId()), send1Json);
							send(socketMap.get(numer0n.getPlayer2().getSocketId()), send2Json);
							numer0n.fin();
						}
					}
					break;
				case COLLECT_NUM_PLAYER_2: //9
				case COLLECT_SEND_NUM: //10
				case ALL_HIT_PLAYER_1: //11
				case ALL_HIT_PLAYER_2: //12
				case ALL_HIT_PLAYER: //13
				case FIN: //14
					break;
				default:
					break;
			}

			System.out.println("[After] " + numer0n.getPhase());
			ULog.webSocketOnMessage("[AFTER_PHASE] " + session.getId(), user, "/GameWebSocket?gameId=" + gameId, numer0n.getPhase().toString());
			if(json != null) sendThisGamePlayer(gameId, json);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Numer0nException e) {
			e.printStackTrace();
		}
	}

	/**
	 * WAIT_SEND_NUM_PLAYER_1(6)
	 * WAIT_SEND_NUM_PLAYER_2(8)
	 * @param number
	 * @param numer0n
	 * @param player
	 * @return json
	 * @throws Numer0nException
	 */
	private Json getHitAndBlow(String number, Numer0n numer0n, Numer0nPlayer player) throws Numer0nException {
		HitAndBlow hab = numer0n.answerNumber(number, numer0n.getPlayerInt(player.getPlayer().getId()));

		Json json = new Json();
		json.user.id = player.getPlayer().getId();
		json.user.name = player.getPlayer().getName();
		json.hab.hit = hab.getHit();
		json.hab.blow = hab.getBlow();
		json.number = number;
		json.message = "log";

		return json;
	}

	/**
	 * 接続確立
	 */
	@Override
 	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.gameId = (String) session.getAttributes().get("gameId");
		this.sessionId = (String) session.getAttributes().get("sessionId");
		Numer0n numer0n = Numer0nData.get(gameId);
		Numer0nPlayer player = null;
		try {
			player = numer0n.setPlayerWebSocket(sessionId, session.getId());
		} catch (Numer0nException e) {
			e.printStackTrace();
		}
		gameMap.put(session.getId(), gameId);
		socketMap.put(session.getId(), new ConcurrentWebSocketSessionDecorator(session, 1000, Integer.MAX_VALUE));
		User user = player.getPlayer();
		System.out.printf("[Game] onOpen: %s(%s): %s\n", user.getName(), user.getId(), session.getId());
		ULog.webSocketConnect("[GAME] " + session.getId(), user, "/GameWebSocket?gameId=" + gameId + "&sessionId=" + sessionId, gameId);

		try {
//			if(numer0n.getPlayer1().getSocketId() != null && numer0n.getPlayer2().getSocketId() != null) {
			if(numer0n.getPhase() != Phase.INIT) {
				sendThisGamePlayer(gameId, getPlayerData(numer0n.getPlayer1().getPlayer()));
				sendThisGamePlayer(gameId, getPlayerData(numer0n.getPlayer2().getPlayer()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(numer0n.getPhase() == Phase.FIN) {
			Json json = new Json();
			json.message = "Close";
			send(socketMap.get(session.getId()), json);
		}
		numer0n.setConnect(numer0n.getPlayerInt(numer0n.getPlayerBySocket(session.getId()).getPlayer().getId()));
	}

	private Json getPlayerData(User user) {
		Json json = new Json();
		json.user.id = user.getId();
		json.user.name = user.getName();
		json.message = "Player";
		return json;
	}

	/**
	 * 接続終了
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		try {
			String gameId = gameMap.get(session.getId());
			Numer0n numer0n = Numer0nData.get(gameId);
			try {
				User user = numer0n.getPlayerBySocket(session.getId()).getPlayer();
				System.out.printf("[Game] onClose: %s(%s): %s\n", user.getName(), user.getId(), session.getId());
				ULog.webSocketDisconnect("[GAME] " + session.getId(), user, "/GameWebSocket?gameId=" + gameId, gameId);
			} catch (Exception e) {
				System.out.printf("[Game] onClose: %s\n", session.getId());
				ULog.webSocketDisconnect("[GAME] " + session.getId(), null, "/GameWebSocket?gameId=" + gameId, gameId);
			}

			try {
//				Numer0nPlayer player = numer0n.getPlayerBySocket(session.getId());
//				Numer0nPlayer otherPlayer = numer0n.getPlayerInt(player.getPlayer().getId()) == 1 ? numer0n.getPlayer2() : numer0n.getPlayer1();
//
//				if(!(numer0n.getPhase() == Phase.FIN || numer0n.getPhase() == Phase.INIT)) {
//					Json json = new Json();
//					json.user.id = player.getPlayer().getId();
//					json.message = "Surrender";
//					send(socketMap.get(otherPlayer.getSocketId()), json);
//					SurrenderScore(numer0n, otherPlayer.getPlayer().getId(), player.getPlayer().getId());
//					numer0n.getDao().insertLog("Surrender", player.getPlayer().getId(), null);
//					try {
//						numer0n.surrender();
//					} catch (Numer0nException e) {
//						e.printStackTrace();
//					}
//				}
			} catch (Exception e) {
			}

			if(numer0n.getPhase() == Phase.FIN) {
				try {
					Numer0nPlayer player = numer0n.getPlayerBySocket(session.getId());
					Numer0nPlayer otherPlayer = numer0n.getPlayerInt(player.getPlayer().getId()) == 1 ? numer0n.getPlayer2() : numer0n.getPlayer1();

					Json json = new Json();
					json.message = "ChatClose";
					send(socketMap.get(otherPlayer.getSocketId()), json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			numer0n.setDisconnect(numer0n.getPlayerInt(numer0n.getPlayerBySocket(session.getId()).getPlayer().getId()));

			if(numer0n.getDisconnect() == 3) {
				if(!(numer0n.getPhase() == Phase.FIN || numer0n.getPhase() == Phase.INIT)) {
					Numer0nPlayer player = numer0n.getPlayerBySocket(session.getId());
					Numer0nPlayer otherPlayer = numer0n.getPlayerInt(player.getPlayer().getId()) == 1 ? numer0n.getPlayer2() : numer0n.getPlayer1();
					SurrenderScore(numer0n, player.getPlayer().getId(), otherPlayer.getPlayer().getId());
					numer0n.getDao().insertLog("Surrender", otherPlayer.getPlayer().getId(), null);
					try {
						numer0n.surrender();
					} catch (Numer0nException e) {
						e.printStackTrace();
					}
				} else {
					numer0n.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void send(ConcurrentWebSocketSessionDecorator session, String message) {
		String gameId = gameMap.get(session.getId());
		Numer0n numer0n = Numer0nData.get(gameId);
		try {
			ULog.webSocketSendMessage("[GAME] " + session.getId(), numer0n.getPlayerBySocket(session.getId()).getPlayer(), "/GameWebSocket", message);
			session.sendMessage(new TextMessage(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void send(ConcurrentWebSocketSessionDecorator session, Json json) {
		send(session, new GsonBuilder().serializeNulls().create().toJson(json));

		WatchWebSocket.sendMessage(gameId);
	}

	public void sendThisGamePlayer(String gameId, String message) {
		Numer0n numer0n = Numer0nData.get(gameId);

		send(socketMap.get(numer0n.getPlayer1().getSocketId()), message);
		send(socketMap.get(numer0n.getPlayer2().getSocketId()), message);
	}

	private void sendThisGamePlayer(String gameId, Json json) {
		sendThisGamePlayer(gameId, new GsonBuilder().serializeNulls().create().toJson(json));

		if(!(json.message.equals("startNumer0n") || json.message.equals("hitNumber"))) WatchWebSocket.sendMessage(gameId);
	}

	private void winLoseScore(Numer0n numer0n, String winPlayerId, String losePlayerId) {
		LeaderBoardDao dao = new LeaderBoardDao();
		RecordDao record = new RecordDao();

		int winPlayerScore = dao.getScore(winPlayerId);
		int losePlayerScore = dao.getScore(losePlayerId);
		int diffScore =  losePlayerScore - winPlayerScore;

		int winPlayerUpdateScore = 30;
		if(diffScore / 100 >= 1) {
			winPlayerUpdateScore += Integer.parseInt(diffScore / 100 + "") * 15;
		}
		dao.updateScore(winPlayerId, winPlayerUpdateScore + winPlayerScore);
		numer0n.getDao().insertLog("SCORE", winPlayerId, (winPlayerUpdateScore > 0 ? "+" : winPlayerUpdateScore == 0 ? "±" : "") + winPlayerUpdateScore);
		record.addWin(winPlayerId);

		int losePlayerUpdateScore = -30;
		if(diffScore / 100 >= 1) {
			losePlayerUpdateScore += Integer.parseInt(diffScore / 100 + "") * -15;
		}
		dao.updateScore(losePlayerId, losePlayerUpdateScore + losePlayerScore);
		numer0n.getDao().insertLog("SCORE", losePlayerId, (losePlayerUpdateScore > 0 ? "+" : losePlayerUpdateScore == 0 ? "±" : "") + losePlayerUpdateScore);
		record.addLose(losePlayerId);

	}

	private void drawScore(Numer0n numer0n, String player1Id, String player2Id) {
		LeaderBoardDao dao = new LeaderBoardDao();
		RecordDao record = new RecordDao();

		int player1Score = dao.getScore(player1Id);
		int player2Score = dao.getScore(player2Id);
		int diffScore = player1Score - player2Score;

		int player1UpdateScore = -15;
		if(diffScore / 100 != 0) {
			int score = Integer.parseInt(diffScore / 100 + "");
			score = score < -15 ? -15 : score;
			player1UpdateScore -= score;
		}

		int player2UpdateScore = -15;
		if(diffScore / 100 != 0) {
			int score = Integer.parseInt(diffScore / 100 + "");
			score = score < -15 ? -15 : score;
			player2UpdateScore -= score;
		}

		dao.updateScore(player1Id, player1Score + player1UpdateScore);
		numer0n.getDao().insertLog("SCORE", player1Id, (player1UpdateScore > 0 ? "+" : player1UpdateScore == 0 ? "±" : "") + player1UpdateScore);
		record.addDraw(player1Id);
		dao.updateScore(player2Id, player2Score + player2UpdateScore);
		numer0n.getDao().insertLog("SCORE", player1Id, (player2UpdateScore > 0 ? "+" : player2UpdateScore == 0 ? "±" : "") + player2UpdateScore);
		record.addDraw(player2Id);
	}

	private void SurrenderScore(Numer0n numer0n, String winPlayerId, String losePlayerId) {
		LeaderBoardDao dao = new LeaderBoardDao();
		RecordDao record = new RecordDao();

		int winPlayerScore = dao.getScore(winPlayerId);
		int losePlayerScore = dao.getScore(losePlayerId);
		int diffScore = losePlayerScore - winPlayerScore;

		int winPlayerUpdateScore = 30;
		if(diffScore / 100 >= 1) {
			winPlayerUpdateScore += Integer.parseInt(diffScore / 100 + "") * 15;
		}
		dao.updateScore(winPlayerId, winPlayerUpdateScore + winPlayerScore);
		numer0n.getDao().insertLog("SCORE", winPlayerId, (winPlayerUpdateScore > 0 ? "+" : winPlayerUpdateScore == 0 ? "±" : "") + winPlayerUpdateScore);
		record.addWin(winPlayerId);

		int losePlayerUpdateScore = -31;
		if(diffScore / 100 >= 1) {
			losePlayerUpdateScore += Integer.parseInt(diffScore / 100 + "") * -15;
		}
		dao.updateScore(losePlayerId, losePlayerUpdateScore + losePlayerScore);
		numer0n.getDao().insertLog("SCORE", losePlayerId, (losePlayerUpdateScore > 0 ? "+" : losePlayerUpdateScore == 0 ? "±" : "") + losePlayerUpdateScore);
		record.addLose(losePlayerId);
		record.addSurrender(losePlayerId);
	}

}
