package model.json;

public class Json {
	//{"user":{"id":"","name":""},"hab":{"hit":0,"blow":0},"number":"","message":""};
	public User user;
	public Hab hab;
	public String number;
	public String message;

	public Json() {
		user = new User();
		hab = new Hab();
	}

}
