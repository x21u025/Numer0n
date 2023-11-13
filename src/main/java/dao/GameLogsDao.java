package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dto.GameLogs;

public class GameLogsDao extends BaseDao {

	public ArrayList<GameLogs> getAllGameLogs() {
		String sql = "SELECT no, date, id FROM gamelogs";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		ArrayList<GameLogs> gameLogs = new ArrayList<GameLogs>();
		for(Map<String, Object> m : list) {
			GameLogs logs = new GameLogs();
			logs.setNo((int) m.get("no"));
			logs.setDate((Timestamp) m.get("date"));
			logs.setId((String) m.get("id"));
			gameLogs.add(logs);
		}
		return gameLogs;
	}

}
