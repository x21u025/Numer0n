package numer0n;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

import dao.GameLogDao;
import dto.User;
import exception.Numer0nException;
import exception.Numer0nNumberException;
import exception.Numer0nPhaseException;
import exception.Numer0nPlayerException;
import numer0n.data.Numer0nData;
import util.Util;

public class Numer0n {
	private final String gameId;
	private Phase phase = Phase.INIT;
	private GameLogDao dao;

	private final Numer0nPlayer player1;
	private final Numer0nPlayer player2;
	private int disconnect = 0;

	public enum Phase {
		INIT(0),					// スタート時
		WAIT_SET_PLYAER(1),			// プレイヤーの入力待ち
		SET_PLAYER_1(2),			// プレイヤー1の入力完了
		SET_PLAYER_2(3),			// プレイヤー2の入力完了
		COLLECT_SET_PLAYER(4),		// プレイヤーの入力完了
		START_GAME(5),				// ゲーム開始
		WAIT_SEND_NUM_PLAYER_1(6),	// プレイヤー1の回答待ち
		COLLECT_NUM_PLAYER_1(7),	// プレイヤー1の回答完了
		WAIT_SEND_NUM_PLAYER_2(8),	// プレイヤー2の回答待ち
		COLLECT_NUM_PLAYER_2(9),	// プレイヤー2の回答完了
		COLLECT_SEND_NUM(10),		// プレイヤーの回答完了
		ALL_HIT_PLAYER_1(11),		// プレイヤー1が当てた
		ALL_HIT_PLAYER_2(12),		// プレイヤー2が当てた
		ALL_HIT_PLAYER(13),			// 同時に当てた
		FIN(14);					// 終了

		private int num;
		private Phase(int num) {
			this.num = num;
		}

		public int getNum() {
			return num;
		}

		Phase next() {
			return next(1);
		}
		Phase next(int add) {
			return get(getNum() + add);
		}
		private Phase get(int num) {
			return Stream.of(Phase.values()).filter(p -> p.getNum() == num).findFirst().get();
		}

		@Override
		public String toString() {
			return super.toString() + "(" + this.getNum() + ")";
		}
	}

	public Numer0n(User player1, User player2) {
		String sha256 = new Random().nextInt(Integer.MAX_VALUE) + "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(new Date().toString().getBytes(StandardCharsets.UTF_8));
			sha256 = String.format("%040x", new BigInteger(1, digest));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		gameId = sha256;
		this.player1 = new Numer0nPlayer(player1);
		this.player2 = new Numer0nPlayer(player2);
		Numer0nData.put(gameId, this);
		dao = new GameLogDao(gameId);
		dao.insertLog("CREATE", null, null);
	}
	public Numer0n(User player) {
		this(player, null);
	}
	@Deprecated
	public Numer0n(Numer0n numer0n, User player2) {
		gameId = numer0n.getGameId();
		player1 = numer0n.getPlayer1();
		this.player2 = new Numer0nPlayer(player2);
		Numer0nData.put(gameId, this);
	}

	public Numer0nPlayer setPlayerWebSocket(String sessionId, String socketId) throws Numer0nException {
		Numer0nPlayer returnPlayer = null;
		User user = Util.getUserBySession(sessionId);
		int player = getPlayerInt(user.getId());
		if(!(player == 1 || player == 2)) throw new Numer0nPlayerException("存在しないプレイヤー番号です");

		if(player == 1) {
			player1.setSocketId(socketId);
			dao.insertLog("PLAYER", player1.getPlayer().getId(), "1");
			returnPlayer = player1;
		}
		if(player == 2) {
			player2.setSocketId(socketId);
			dao.insertLog("PLAYER", player2.getPlayer().getId(), "2");
			returnPlayer = player2;
		}

		if(player1.getSocketId() != null && player2.getSocketId() != null) {
			try {
				start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnPlayer;
	}

	public Numer0nPlayer getPlayerBySocket(String socketId) {
		if(player1.getSocketId().equals(socketId)) return player1;
		if(player2.getSocketId().equals(socketId)) return player2;
		return null;
	}

	public Numer0nPlayer getPlayer1() {
		return player1;
	}
	public Numer0nPlayer getPlayer2() {
		return player2;
	}
	public String getGameId() {
		return gameId;
	}
	public Phase getPhase() {
		return phase;
	}
	public GameLogDao getDao() {
		return dao;
	}
	public boolean isPlayer(String playerId) {
		if(getPlayer1().getPlayer().getId().equals(playerId)) return true;
		if(getPlayer2().getPlayer().getId().equals(playerId)) return true;
		return false;
	}

	public void start() throws Numer0nPhaseException {
		if(phase != Phase.INIT) throw new Numer0nPhaseException("フェーズがおかしいです");
		phase = Phase.WAIT_SET_PLYAER;
	}

	private void setPhase(Phase phase) {
		this.phase = phase;
	}

	/**
	 * WAIT_SET_PLYAER(1)
	 * SET_PLAYER_1(2)
	 * SET_PLAYER_2(3)
	 * で使用可能
	 * @param number 三桁の数字
	 * @param player プレイヤー1か2か
	 * @return boolean 次に進んでよいか
	 * @throws Numer0nException
	 */
	public boolean setNumber(String number, int player) throws Numer0nException {
		if(!(phase.getNum() >= 1 && phase.getNum() <= 3)) throw new Numer0nPhaseException("フェーズがおかしいです");
		if(!checkNumber(number)) throw new Numer0nNumberException("数字が重複しています");
		if(!(player == 1 || player == 2)) throw new Numer0nPlayerException("存在しないプレイヤー番号です");

		if(player == 1) player1.setNumber(number);
		if(player == 2) player2.setNumber(number);
		setPhase(phase.next(player));

		return getPhase() == Phase.COLLECT_SET_PLAYER;
	}

	/**
	 * WAIT_SEND_NUM_PLAYER_1(6)
	 * WAIT_SEND_NUM_PLAYER_2(8)
	 * で使用可能
	 * @param number 三桁の数字
	 * @param player プレイヤー1か2か
	 * @throws Numer0nException
	 */
	public HitAndBlow answerNumber(String number, int player) throws Numer0nException {
		if(!(phase.getNum() == 6 || phase.getNum() == 8)) throw new Numer0nPhaseException("フェーズがおかしいです");
		if(!checkNumber(number)) throw new Numer0nNumberException("数字が重複しています");
		if(!(player == 1 || player == 2)) throw new Numer0nPlayerException("存在しないプレイヤー番号です");

		setPhase(phase.next());
		if(player == 1) {
			HitAndBlow hab = HitAndBlow.createHitAndBlow(player2.getNumber(), number);
			if(hab.isAllHit()) player1.setAllHit(true);
//			dao.insertLog("HIT_AND_BLOW_PLAYER_1", player1.getPlayer().getId(), hab.getHit() + "-" + hab.getBlow());
			return hab;
		}
		if(player == 2) {
			HitAndBlow hab = HitAndBlow.createHitAndBlow(player1.getNumber(), number);
			if(hab.isAllHit()) player2.setAllHit(true);
//			dao.insertLog("HIT_AND_BLOW_PLAYER_2", player2.getPlayer().getId(), hab.getHit() + "-" + hab.getBlow());
			return hab;
		}
		return null;
	}

	/**
	 * COLLECT_SEND_NUM(10)
	 * で使用可能
	 * @throws Numer0nException
	 */
	public void checkFin() throws Numer0nException {
		if(!(phase.getNum() == 10)) throw new Numer0nPhaseException("フェーズがおかしいです");

		if(player1.isAllHit() && player2.isAllHit()) {
			setPhase(Phase.ALL_HIT_PLAYER);
		} else if(player1.isAllHit()) {
			setPhase(Phase.ALL_HIT_PLAYER_1);
		} else if(player2.isAllHit()) {
			setPhase(Phase.ALL_HIT_PLAYER_2);
		} else {
			setPhase(Phase.WAIT_SEND_NUM_PLAYER_1);
		}
	}

	/**
	 * ALL_HIT_PLAYER_1(11)
	 * ALL_HIT_PLAYER_2(12)
	 * ALL_HIT_PLAYER(13)
	 * で使用可能
	 * @throws Numer0nException
	 */
	public void fin() throws Numer0nException {
		if(!(phase.getNum() >= 11 && phase.getNum() <= 13)) throw new Numer0nPhaseException("フェーズがおかしいです");

		setPhase(Phase.FIN);
		dao.insertLog("FIN", null, null);
	}

	public void surrender() throws Numer0nException {
		setPhase(Phase.FIN);
		Numer0nData.remove(gameId);
		dao.insertLog("FIN", null, null);
	}

	public void close() {
		Numer0nData.remove(gameId);
	}

	/**
	 * 次のフェーズに進んでもいいか
	 * @return
	 */
	public boolean hasNext() {
		return Util.intOrEquals(phase.getNum(), 4, 5, 6, 7, 8, 9);
	}

	/**
	 * 次のフェーズに進みます
	 * (進めない場合はNumer0nPhaseException)
	 * @throws Numer0nException
	 */
	public void next() throws Numer0nException {
		if(!hasNext()) throw new Numer0nPhaseException("次のフェーズに進めません");

		setPhase(phase.next());
	}

	private boolean checkNumber(String number) {
		char[] chars = number.toCharArray();
		ArrayList<Character> hasChars = new ArrayList<>();
		for(char c : chars) {
			if(hasChars.contains(c)) return false;
			hasChars.add(c);
		}
		return true;
	}

	public int getPlayerInt(String userId) {
		if(player1.getPlayer().getId().equals(userId)) return 1;
		if(player2.getPlayer() != null && player2.getPlayer().getId().equals(userId)) return 2;

		return 0;
	}

	public void setConnect(int connect) {
		if(this.disconnect == 1 && disconnect == 1) this.disconnect -= disconnect;
		if(this.disconnect == 2 && disconnect == 2) this.disconnect -= disconnect;

	}
	public void setDisconnect(int disconnect) {
		if(this.disconnect == 1 && disconnect == 1) return;
		if(this.disconnect == 2 && disconnect == 2) return;
		this.disconnect += disconnect;
	}
	public int getDisconnect() {
		return disconnect;
	}
}
