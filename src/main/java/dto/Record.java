package dto;

public class Record {

	private String id;
	private int win;
	private int lose;
	private int draw;
	private int surrender;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	public int getLose() {
		return lose;
	}
	public void setLose(int lose) {
		this.lose = lose;
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getSurrender() {
		return surrender;
	}
	public void setSurrender(int surrender) {
		this.surrender = surrender;
	}

}
