package world;

public class Air {
	public static float TOTAL_GLOBAL_CARBON = 1000000;
	private float carbon;
	
	public Air() {
		carbon = TOTAL_GLOBAL_CARBON;
	}

	public float getCarbon() {
		return carbon;
	}

	public void addCarbon(float carbon) {
		this.carbon += carbon;
	}
	
	public float harvest(float howMuch) {
		float oldCarbon = carbon;
		carbon -= howMuch;
		if (carbon < 0) {
			carbon = 0;
		}
		return oldCarbon - carbon;
	}
	
}
