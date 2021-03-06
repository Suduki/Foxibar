package agents;

import talents.Talents;
import constants.Constants;
import world.World;

public class Stomach {

	public static final float MAX_FULLNESS = 50;
	public static final float FAT_ON_BIRTH = 1;

	float energyCost;
	public float fiber;
	public float grass;
	public float blood;
	public float fat;
	private float pFiber;
	private float pGrass;
	private float pBlood;


	public void inherit(Talents skillSet) {
		empty();
		fat = FAT_ON_BIRTH;
		pFiber = skillSet.get(Talents.DIGEST_FIBER);
		pGrass = skillSet.get(Talents.DIGEST_GRASS);
		pBlood = skillSet.get(Talents.DIGEST_BLOOD);
	}


	/**
	 * Called at the end of round to digest blood/grass and create fat.
	 * Also burns the fat.
	 * @return fat > 0, whether this animal is starving
	 */
	public boolean stepStomach() {
		energyCost += 1f;
		digest();
		burnFat();
		checkFullness();
		return fat > 0;
	}

	private void checkFullness() {
		float total = getMass();
		if (total > MAX_FULLNESS) { // Stomach is full
			fiber = fiber * MAX_FULLNESS/total;
			grass = grass * MAX_FULLNESS/total;
			blood = blood * MAX_FULLNESS/total;
			fat = fat * MAX_FULLNESS/total;
		}
	}

	/**
	 * Digests
	 */
	private void digest() {
		float totalFullness = grass + blood + fiber;
		fat += pGrass * grass;
		fat += pBlood * blood;
		fat += pFiber * fiber;
		grass = 0;
		blood = 0;
		fiber = 0;

	}
	public final static float FAT_TO_ENERGY_FACTOR = 0.05f;
	private void burnFat() {
		fat -= energyCost*FAT_TO_ENERGY_FACTOR;
		energyCost = 0;
	}

	public float getMass() {
		return fat + grass + blood + fiber;
	}

	public void empty() {
		fat = 0;
		grass = 0;
		blood = 0;
		fiber = 0;
	}

	public void addBlood(float amount) {
		blood += amount;
	}
	
	public void addGrass(float amount) {
		grass += amount;
	}
	
	public void addFiber(float amount) {
		fiber += amount;
	}

	public float getRelativeFullness() {
		return getMass() / MAX_FULLNESS;
	}

	public boolean canHaveBaby(float birthHungerCost) {
		return (fat / FAT_TO_ENERGY_FACTOR) > (birthHungerCost*2);
	}

	private static final float energyCostAtMaxSpeed = 5f;
	public static final float MUTATION = 0.2f;
	public void addRecoverCost(float speed) {
		float c = energyCostAtMaxSpeed / (-1 + 1/Constants.Talents.MIN_SPEED);
		float b = -2 * c;
		float a = c / Constants.Talents.MIN_SPEED;
		energyCost += a*speed*speed + b*speed + c;
	}

	public static final int GRASS = 0;
	public static final int BLOOD = 1;

	public float add(final int STOMACH_ID, float amount) {
		switch (STOMACH_ID) {
		case GRASS:
			addGrass(amount);
			break;
		case BLOOD:
			addBlood(amount);
			break;
		default:
			System.err.println("in Stomach.add, trying to add something weird...");
		}
		return amount;
	}


	public boolean isFull() {
		return getRelativeFullness() > 0.9f;
	}

}

