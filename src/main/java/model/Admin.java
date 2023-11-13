package model;

import dto.LeaderBoard;
import dto.Record;
import dto.User;

public class Admin {

	private User user;
	private Record record;
	private LeaderBoard leaderBoard;

	public Admin(User user, Record record, LeaderBoard leaderBoard) {
		this.user = user;
		this.record = record;
		this.leaderBoard = leaderBoard;
	}

	public User getUser() {
		return user;
	}
	public Record getRecord() {
		return record;
	}
	public LeaderBoard getLeaderBoard() {
		return leaderBoard;
	}

}
