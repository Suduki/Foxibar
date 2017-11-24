package agents;

public class Stamina {
	/**
	 * Value between [0,2).
	 * Used together with speed to determine if we should move next turn.
	 * If above 1, move & reduce by 1.
	 * If below 1, sleep and increase by speed.
	 */
	private float readiness;
	/**
	 * Value between (-1, MAX_STAMINA]
	 */
	private float stamina;
	
	/**
	 * Determined from species.
	 * Ranges between [0,MAX_STAMINA]
	 */
	private float maxStamina;
	private float restCapability;
	
	private final float MAX_STAMINA = 100;
	private final float MAX_STAMINA_COST = 2;
	private final float MIN_SPEED = 0.1f;
	
	/**
	 * @param restCapability ranges between [0,MAX_STAMINA]. Determines how much energy is regained when sleeping one time step.
	 * @param relStaminaMax ranges between [0,1]. Determines how large stamina pool the animal has.
	 */
	public void init(float restCapability, float relStaminaMax) {
		readiness = 0;
		stamina = 0;
		this.restCapability = restCapability;
		this.maxStamina = relStaminaMax * MAX_STAMINA;
	}
	
	/**
	 * Update Stamina depending on Animal speed.
	 * @return if sleeping; true
	 */
	public boolean isSleeping(float speed) {
		if (speed < MIN_SPEED) {speed = MIN_SPEED;}
		stamina -= speedToStamina(speed);
		readiness += speed;
		if (readiness > 1 && stamina > 0) {
			// Move!
			readiness--;
			return false;
		}
		else {
			// Sleep!
			stamina += restCapability;
			return true;
		}
		
	}

	private final float a = MAX_STAMINA_COST; // Used in 2nd degreee poly in speedToStamina
	
	/**
	 * Converts speed to stamina drain
	 * If speed = 0; 0 stamina cost.
	 * If speed = 1; MAX stamina cost.
	 * @param speed
	 * @return
	 */
	private float speedToStamina(float speed) {
		return a*speed*speed;
	}
	
	/**
	 * @return A normalized stamina, between [0,1].
	 */
	public float getRelativeStamina() {
		if (stamina > 0) {
			return stamina / maxStamina;
		}
		else {
			return 0;
		}
	}
}
