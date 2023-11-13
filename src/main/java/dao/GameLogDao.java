package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dto.GameLog;


public class GameLogDao extends BaseDao {

	private String gameId;

	public GameLogDao(String gameId) {
		this.gameId = gameId;
		String sql = "INSERT INTO gamelogs(date, id) VALUES(now(), ?)";
		jdbcTemplate.update(sql, gameId);
	}
	public GameLogDao() {}

	public ArrayList<GameLog> getLogs(String gameId) {
		String sql = "SELECT no, gameid, date, process, player, number FROM gamelog WHERE gameid = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, gameId);

		ArrayList<GameLog> gameLogs = new ArrayList<GameLog>();
		for(Map<String, Object> m : list) {
			GameLog log = new GameLog();
			log.setNo((int) m.get("no"));
			log.setGameId((String) m.get("gameid"));
			log.setDate((Timestamp) m.get("date"));
			log.setProcess((String) m.get("process"));
			log.setPlayer((String) m.get("player"));
			log.setNumber((String) m.get("number"));
			gameLogs.add(log);
		}
		return gameLogs;
	}

	@Deprecated
	public ArrayList<GameLog> getLogs() {
		return getLogs(gameId);
	}

	public void insertLog(String process, String playerId, String number) {
		String sql = "INSERT INTO gamelog(gameid, date, process, player, number) VALUES(?, now(), ?, ?, ?)";
		jdbcTemplate.update(sql, gameId, process, playerId, number);
	}

	public ArrayList<GameLog> getAllLogs() {
		String sql = "SELECT no, gameid, date, process, player, number FROM gamelog";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		ArrayList<GameLog> gameLogs = new ArrayList<GameLog>();
		for(Map<String, Object> m : list) {
			GameLog log = new GameLog();
			log.setNo((int) m.get("no"));
			log.setGameId((String) m.get("gameid"));
			log.setDate((Timestamp) m.get("date"));
			log.setProcess((String) m.get("process"));
			log.setPlayer((String) m.get("player"));
			log.setNumber((String) m.get("number"));
			gameLogs.add(log);
		}
		return gameLogs;
	}

}
