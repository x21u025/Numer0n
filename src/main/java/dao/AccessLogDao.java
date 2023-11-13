package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import dto.AccessLog;

@Repository
@Service
public class AccessLogDao extends BaseDao {

	private static AccessLogDao dao = new AccessLogDao();

	public static void insertLog(String process, String ip, String session, String user, String page, String message) {
		dao._insertLog(process, ip, session, user, page, message);
	}
	public static ArrayList<AccessLog> getAllLogs() {
		return dao._getAllLogs();
	}

	public static ArrayList<AccessLog> getLogs(String page, String size) {
		int _page = Integer.parseInt(page);
		int _size = Integer.parseInt(size);
		int count = dao.getRowsCount();
		if(_size >= count) return dao._getAllLogs();
		if(_page * _size > count) {
			_page = count / _size + 1;
		}
		if(_page * _size >= count) return dao._getLogs((_page - 1) * _size + 1 + "", count + "");
		return dao._getLogs((_page - 1) * _size + 1 + "", _page * _size + "");
	}

	private AccessLogDao() {}

	private void _insertLog(String process, String ip, String session, String user, String page, String message) {
		String sql = "INSERT INTO accesslog(date, process, ip, session, user, page, message) VALUES(now(), ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, process, ip, session, user, page, message);
	}

	private ArrayList<AccessLog> _getAllLogs() {
		String sql = "SELECT no, date, process, ip, session, `user`, page, message FROM accesslog";

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		ArrayList<AccessLog> logs = new ArrayList<AccessLog>();
		for(Map<String, Object> m : list) {
			AccessLog log = new AccessLog();
			log.setNo((int) m.get("no"));
			log.setDate((Timestamp) m.get("date"));
			log.setProcess((String) m.get("process"));
			log.setIp((String) m.get("ip"));
			log.setSession((String) m.get("session"));
			log.setUser((String) m.get("user"));
			log.setPage((String) m.get("page"));
			log.setMessage((String) m.get("message"));
			logs.add(log);
		}
		return logs;
	}

	private ArrayList<AccessLog> _getLogs(String min, String max) {
		String sql = "SELECT no, date, process, ip, session, `user`, page, message FROM accesslog WHERE no >= ? AND no <= ?";

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, min, max);

		ArrayList<AccessLog> logs = new ArrayList<AccessLog>();
		for(Map<String, Object> m : list) {
			AccessLog log = new AccessLog();
			log.setNo((int) m.get("no"));
			log.setDate((Timestamp) m.get("date"));
			log.setProcess((String) m.get("process"));
			log.setIp((String) m.get("ip"));
			log.setSession((String) m.get("session"));
			log.setUser((String) m.get("user"));
			log.setPage((String) m.get("page"));
			log.setMessage((String) m.get("message"));
			logs.add(log);
		}
		return logs;
	}

	private int getRowsCount() {
		String sql = "SELECT count(*) FROM accesslog";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(list.size() == 1) {
			return Math.toIntExact((long) list.get(0).get("COUNT(*)"));
		}
		return 0;
	}

}
