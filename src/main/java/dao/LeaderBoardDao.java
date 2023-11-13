package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dto.LeaderBoard;

public class LeaderBoardDao extends BaseDao {

	public ArrayList<LeaderBoard> nameAndScore() {
		String sql = "SELECT id, score FROM score ORDER BY score DESC;";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		UserDao userDao = new UserDao();
		ArrayList<LeaderBoard> leaderBoards = new ArrayList<LeaderBoard>();
		for(Map<String, Object> m : list) {
			String id = (String) m.get("id");
			if(userDao.isHiddenUserById(id)) continue;

			LeaderBoard lb = new LeaderBoard();
			lb.setName(userDao.findNameById(id));
			lb.setScore((int) m.get("score"));
			leaderBoards.add(lb);
		}
		return leaderBoards;
	}

	public int getScore(String id) {
		String sql = "SELECT score FROM score WHERE id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);

		if(!list.isEmpty()) {
			return (int) list.get(0).get("score");
		}
		return Integer.MIN_VALUE;
	}

	public void createScore(String id) {
		String sql = "INSERT INTO score VALUES(?, 1500)";
		jdbcTemplate.update(sql, id);
	}

	public void updateScore(String id, int score) {
		String sql = "UPDATE score SET score = ? WHERE id = ?";
		jdbcTemplate.update(sql, score, id);
	}

	public ArrayList<LeaderBoard> getAllScore() {
		String sql = "SELECT id, score FROM score";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		ArrayList<LeaderBoard> scores = new ArrayList<LeaderBoard>();
		for(Map<String, Object> m : list) {
			LeaderBoard score = new LeaderBoard();
			score.setName((String) m.get("id"));
			score.setScore((int) m.get("score"));
			scores.add(score);
		}
		return scores;
	}
}
