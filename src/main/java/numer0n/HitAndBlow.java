package numer0n;

public class HitAndBlow {

	private int hit;
	private int blow;
	private boolean allHit;

	public HitAndBlow(int hit, int blow) {
		this.hit = hit;
		this.blow = blow;
	}

	public int getHit() {
		return hit;
	}
	public int getBlow() {
		return blow;
	}
	public boolean isAllHit() {
		return allHit;
	}

	/**
	 *
	 * @param ans 答え
	 * @param res 回答
	 * @return
	 */
	public static HitAndBlow createHitAndBlow(String ans, String res) {
		char[] answers = ans.toCharArray();
		char[] responses = res.toCharArray();

		int hit = 0;
		int blow = 0;

		for(int i = 0; i < answers.length; i++) {
			for(int n = 0; n < responses.length; n++) {
				if(answers[i] == responses[n]) {
					if(i == n) {
						hit++;
					} else {
						blow++;
					}
				}
			}
		}
		HitAndBlow hab = new HitAndBlow(hit, blow);
		if(ans.length() == hab.getHit()) hab.allHit = true;
		return hab;
	}

}
