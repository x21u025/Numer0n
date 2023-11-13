package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import dto.ChatLog;

public class ChatLogDao extends BaseDao {

	public void insertLog(String playerId, String message) {
		String sql = "INSERT INTO chatlog(date, player, message) VALUES(now(), ?, ?)";
		jdbcTemplate.update(sql, playerId, message);
	}

	public ArrayList<ChatLog> getAllLogs() {
		String sql = "SELECT no, date, player, message FROM chatlog";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		ArrayList<ChatLog> chatLogs = new ArrayList<ChatLog>();
		for(Map<String, Object> m : list) {
			ChatLog log = new ChatLog();
			log.setNo((int) m.get("no"));
			log.setDate((Timestamp) m.get("date"));
			log.setPlayer((String) m.get("player"));
			log.setMessage((String) m.get("message"));
			chatLogs.add(log);
		}
		return chatLogs;
	}

	public ArrayList<ChatLog> getLogs(int limit) {
		String sql = "SELECT no, date, player, message FROM chatlog ORDER BY no DESC LIMIT ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, limit);

		ArrayList<ChatLog> chatLogs = new ArrayList<ChatLog>();
		for(Map<String, Object> m : list) {
			ChatLog log = new ChatLog();
			log.setNo((int) m.get("no"));
			log.setDate((Timestamp) m.get("date"));
			log.setPlayer((String) m.get("player"));
			log.setMessage((String) m.get("message"));
			chatLogs.add(log);
		}
		Collections.reverse(chatLogs);
		return chatLogs;
	}

}
