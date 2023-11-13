package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dto.Record;


public class RecordDao extends BaseDao {

	public Record findRecordById(String id) {
		String sql = "SELECT id, win, lose, draw, surrender FROM record WHERE id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
		if(list.size() == 1) {
			Map<String, Object> m = list.get(0);
			Record record = new Record();
			record.setId(id);
			record.setWin((int) m.get("win"));
			record.setLose((int) m.get("lose"));
			record.setDraw((int) m.get("draw"));
			record.setSurrender((int) m.get("surrender"));
			return record;
		}
		return null;
	}

	public void addWin(String id) {
		add(id, "win");
	}
	public void addLose(String id) {
		add(id, "lose");
	}
	public void addDraw(String id) {
		add(id, "draw");
	}
	public void addSurrender(String id) {
		add(id, "surrender");
	}
	private void add(String id, String type) {
		String sql = "UPDATE record SET " + type + " = " + type + " + 1 WHERE id = ?";
		jdbcTemplate.update(sql, id);
	}

	public void createRecord(String id) {
		String sql = "INSERT INTO record(id) VALUES(?)";
		jdbcTemplate.update(sql, id);
	}

	public ArrayList<Record> getAllRecord() {
		String sql = "SELECT id, win, lose, draw, surrender FROM record";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		ArrayList<Record> records = new ArrayList<Record>();
		for(Map<String, Object> m : list) {
			Record record = new Record();
			record.setId((String) m.get("id"));
			record.setWin((int) m.get("win"));
			record.setLose((int) m.get("lose"));
			record.setDraw((int) m.get("draw"));
			record.setSurrender((int) m.get("surrender"));
			records.add(record);
		}
		return records;
	}

}
