package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dto.User;


public class UserDao extends BaseDao {

	public User findByIdAndPassword(String id, String pass) {
		String sql = "SELECT id, name, pass, admin, hidden FROM user WHERE id = ? AND pass = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id, pass);
		if(list.size() == 1) {
			Map<String, Object> m = list.get(0);
			User user = new User();
			user.setId(id);
			user.setPass(pass);
			user.setName((String) m.get("name"));
			user.setAdmin(((int) m.get("admin")) == 1);
			user.setHidden(((int) m.get("hidden")) == 1);
			return user;
		}
		return null;
	}

	public String findNameById(String id) {
		String sql = "SELECT name FROM user WHERE id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
		if(list.size() == 1) {
			return (String) list.get(0).get("name");
		}
		return null;
	}

	public boolean isHiddenUserById(String id) {
		String sql = "SELECT hidden FROM user WHERE id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
		if(list.size() == 1) {
			return ((int) list.get(0).get("hidden")) == 1;
		}
		return false;
	}

	public void setHiddenById(String id, boolean hidden) {
		String sql = "UPDATE user SET hidden = " + hidden + " WHERE id = ?";
		jdbcTemplate.update(sql, id);
	}

	public User findUserById(String id) {
		String sql = "SELECT id, name, pass, admin, hidden FROM user WHERE id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
		if(list.size() == 1) {
			Map<String, Object> m = list.get(0);
			User user = new User();
			user.setId(id);
			user.setPass((String) m.get("pass"));
			user.setName((String) m.get("name"));
			user.setAdmin(((int) m.get("admin")) == 1);
			user.setHidden(((int) m.get("hidden")) == 1);
			return user;
		}
		return null;
	}

	public boolean registerByIdAndPassAndName(String id, String pass, String name) {
		String sql = "INSERT INTO user(id, pass, name) VALUES(?, ?, ?)";
		int count = jdbcTemplate.update(sql, id, pass, name);
		return count == 1;
	}

	public ArrayList<User> getAllUser() {
		String sql = "SELECT id, name, pass, admin, hidden FROM user";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		ArrayList<User> users = new ArrayList<User>();
		for(Map<String, Object> m : list) {
			User user = new User();
			user.setId((String) m.get("id"));
			user.setPass((String) m.get("pass"));
			user.setName((String) m.get("name"));
			user.setAdmin(((int) m.get("admin")) == 1);
			user.setHidden(((int) m.get("hidden")) == 1);
			users.add(user);
		}
		return users;
	}

	public boolean isId(String id) {
		return findNameById(id) != null;
	}

}
