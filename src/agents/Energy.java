package agents;

public class Energy {

	//**************** STATIC STUFF ****************\\
	public static float ENERGY_MAX = 500;
	private static float ENERGY_MIN = 0;
	
	private static float ENERGY_MAX_COST  = 2f;
	private static float ENERGY_MAX_REGEN = 1f;
	private static float SPEED_MAX        = 1f;
	private static float SPEED_MIN        = 0.1f;
	
	private static float K = (ENERGY_MAX_REGEN + ENERGY_MAX_COST) / (SPEED_MAX);
	private static float M = (ENERGY_MAX_COST - K * SPEED_MAX);
	
	public static float speedToEnergy(float speed) {
		return K * speed + M;
	}
	
	//**************** NON-STATIC STUFF ****************\\
	/**
	 * A value between ENERGY_MIN -> ENERGY_MAX
	 * determines how well rested the animal is.
	 */
	public float energy;
	
	/**
	 * A value between 0 -> 1
	 * determines if next turn is ok to move.
	 * cost of one move is (1 - speed)
	 */
	private float recover;
	
	public Energy() {
		init();
	}
	public void init() {
		energy = 1;
	}

	private boolean hasEnoughEnergyToMove() {
		return energy > ENERGY_MIN;
	}
	
	/**
	 * Steps the energy level of the animal.
	 * 
	 * @param speed; between 0->1, Determines how much to restore the recover level.
	 *     A high speed costs more energy than a low speed.
	 * @return true if energy level is sufficient to move, 
	 *     false if energy level or recover level is too low
	 */
	public boolean canMove(float speed, float speciesFactor) {
		if (speed < SPEED_MIN) {speed = SPEED_MIN;}
		if (energy > ENERGY_MAX) {energy = ENERGY_MAX;}
		if (hasEnoughEnergyToMove()) {
			energy -= speedToEnergy(speed);
			return true;
		}
		else {
			energy -= speedToEnergy(SPEED_MIN);
			return false;
		}
	}
}
