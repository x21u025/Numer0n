package numer0n;

import dto.User;

public class Numer0nPlayer {

	private final User player;
	private String socketId;

	private String number;
	private boolean allHit;

	public Numer0nPlayer(User player) {
		this.player = player;
	}

	public void setSocketId(String socketId) {
		this.socketId = socketId;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public void setAllHit(boolean allHit) {
		this.allHit = allHit;
	}

	public User getPlayer() {
		return player;
	}
	public String getSocketId() {
		return socketId;
	}
	public String getNumber() {
		return number;
	}
	public boolean isAllHit() {
		return allHit;
	}

}
