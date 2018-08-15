package agents;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runners.JUnit4;

import constants.Constants;
import world.World;

public class Stomach {

	public static final float MAX_FULLNESS = 100;
	
	float energyCost;
	public float fiber;
	public float blood;
	public float fat;
	float p; // -1 for bloodling, 1 for grassler
	private float pFiber;
	private float pBlood;
	
	public void inherit(float p) {
		empty();
		this.p = p;
		pFiber = mattiasFunction(p);
		pBlood = mattiasFunction(-p);
	}
	
	private static final float a = 0.2f;
	private static final float b = 1f;
	private static final float c = 0.2f;
	private float mattiasFunction(float p2) {
		return (float) (a * Math.exp(b*p2) + c);
	}
//	@Test
	public void testMattiasFunction() { //TODO: Låt Mattias styra upp detta.....
		System.out.println("Mattias Function Test:" +mattiasFunction(-1) +" " + mattiasFunction(0) + " " + mattiasFunction(1));
		org.junit.Assert.assertTrue(mattiasFunction(-1) < 0.5f);
		org.junit.Assert.assertTrue(mattiasFunction(1) > 0.5f);
		org.junit.Assert.assertTrue(mattiasFunction(0) < 0.6f);
	}

	/**
	 * Called at the end of round to digest blood/grass and create fat.
	 * Also burns the fat.
	 * @return fat > 0, whether this animal is starving
	 */
	public boolean stepStomach() {
		energyCost += 1;
		digest();
		burnFat();
		checkFullness();
		return fat > 0;
	}

	private void checkFullness() {
		float digestAmount = getMass() - MAX_FULLNESS;
		if (digestAmount > 0) {
			float total = getMass();
			fiber -= fiber * digestAmount / MAX_FULLNESS;
			blood -= blood * digestAmount / MAX_FULLNESS;
			fat -= fat * digestAmount / MAX_FULLNESS;
		}
	}

	/**
	 * Digests
	 */
	private static final float DIGEST_AMOUNT = 1f; //TODO: styr upp konstanter som denna.
	private void digest() {
		float totalFullness = fiber + blood;
		if (totalFullness > DIGEST_AMOUNT ) {
			
			energyCost += pFiber * fiber * DIGEST_AMOUNT / totalFullness;
			energyCost += pBlood * blood * DIGEST_AMOUNT / totalFullness;
			
			fiber -= fiber * DIGEST_AMOUNT / totalFullness;
			blood -= blood * DIGEST_AMOUNT / totalFullness;
			
			fat += DIGEST_AMOUNT;
		}
		else {
			energyCost += pFiber * fiber;
			energyCost += pBlood * blood;
			fiber = 0;
			blood = 0;
			
			fat += totalFullness;
		}
		
	}
	private final float fatToEnergyFactor = 0.001f;
	private void burnFat() {
		fat -= energyCost*fatToEnergyFactor;
		energyCost = 0;
	}
	
	public float getMass() {
		return fat + fiber + blood;
	}

	public void empty() {
		fat = 0;
		fiber = 0;
		blood = 0;
	}

	public void addFat(float amount) {
		fat += amount;
	}
	public void addBlood(float amount) {
		blood += amount;
	}
	public void addFiber(float amount) {
		fiber += amount;
	}

	public float getRelativeFullness() {
		return getMass() / MAX_FULLNESS;
	}

	public boolean canHaveBaby(int birthHungerCost) {
		return (fat / fatToEnergyFactor) > birthHungerCost;
	}

}

