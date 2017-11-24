package agents;

import sun.security.ssl.Debug;

public class Stomach {

	private static final float MAX_LIMIT = 100;
	
	private static final int BIRTH_HUNGER = 60;
	private static final int HUNGRY_HUNGER = 100;
	private static final int BIRTH_HUNGER_COST = 80;
	
	/**
	 * To be called when animal is born.
	 * @param digestionMagnitude Species related.
	 * @param bloodEffectivity   Species related. Should be high for bloodlings.
	 * @param grassEffectivity   Species related. Should be high for grasslers. 
	 */
	public void init(float digestionMagnitude, float bloodEffectivity,
			float grassEffectivity) {
		this.digestionMagnitude = digestionMagnitude;
		this.bloodEffectivity = bloodEffectivity;
		this.grassEffectivity = grassEffectivity;
		this.bloodAmount = 0;
		this.grassAmount = 0;
		
		hunger = BIRTH_HUNGER;
	}

	private float bloodAmount;
	private float grassAmount;
	private float totalAmount() {return bloodAmount+grassAmount;}
	
	/**
	 * How much the animal can digest in one time step.
	 */
	private float digestionMagnitude; //Should this be related to harvestSkill?
	
	/**
	 * How much hunger blood yields
	 */
	private float bloodEffectivity;
	/**
	 * How much hunger grass yields
	 */
	private float grassEffectivity;
	
	/**
	 * Determines how hungry the animal is.
	 * If this reaches 0, the animal dies.
	 */
	private float hunger;
	
	/**
	 * Is stomach full?
	 */
	public boolean isFull;

	private float sleepCost = 0.2f;

	/**
	 * Digests what is inside the stomach.
	 * 
	 * @return gained energy; to be used with hunger.
	 */
	public void digest() {
		// Digest blood
		float bloodDigestion = 0;
		if (bloodAmount > 0) {
			bloodDigestion = digestionMagnitude * bloodAmount / totalAmount();
			if (bloodAmount < bloodDigestion) {
				// Out of blood in stomach.
				bloodDigestion = bloodAmount;
				bloodAmount = 0;
			}
			else {
				bloodAmount -= bloodDigestion;	
			}
		}
		
		// Digest grass
		float grassDigestion = 0;
		if (grassAmount > 0) {
			grassDigestion = digestionMagnitude * grassAmount / totalAmount();
			if (grassAmount < grassDigestion) {
				// Out of grass in stomach.
				grassDigestion = grassAmount;
				grassAmount = 0;
			}
			else {
				grassAmount -= grassDigestion;	
			}
		}

		// Digesting makes you less hungry.
		hunger += bloodDigestion * bloodEffectivity + grassDigestion * grassEffectivity;
	}
	
	/**
	 * Steps hunger and checks if animal should continue living.
	 * @param didMove 
	 * @return true if animal is still alive, false if animal should die.
	 */
	public boolean stepHunger(boolean didMove) {
		if (didMove) {
			hunger--;
		}
		else {
			hunger -= sleepCost ;
		}
		return hunger > 0;
	}
	/**
	 * Fills the stomach
	 * @param grass
	 * @param blood
	 */
	public void fill(float grass, float blood) {
		grassAmount += grass;
		bloodAmount += blood;
		float t = totalAmount() / MAX_LIMIT;
		if (t > 1) {
			grassAmount /= t;
			bloodAmount /= t;
			isFull = true;
		}
		else {
			isFull = false;
		}
	}

	/**
	 * @return how full the stomach is from 0->1
	 */
	public float getRelativeHunger() {
		return totalAmount() / MAX_LIMIT;
	}

	/**
	 * Should trigger different behaviours when over HUNGRY_HUNGER limit.
	 * @return true if hungry
	 */
	public boolean isHungry() {
		return hunger < HUNGRY_HUNGER;
	}

	/**
	 * Giving birth costs a lot of hunger.
	 */
	public void giveBirth() {
		hunger -= BIRTH_HUNGER_COST;
	}
}
