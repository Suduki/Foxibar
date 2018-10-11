package agents;

import constants.Constants;
import world.World;

public class Stomach {

	public static final float MAX_FULLNESS = 50;
	public static final float FAT_ON_BIRTH = 1;
	
	float energyCost;
	public float fiber;
	public float blood;
	public float fat;
	float p;
	private float pFiber;
	private float pBlood;
	
	
	public void inherit(float p, float mutation) {
		empty();
		fat = FAT_ON_BIRTH;
		
		this.p = p + Agent.rand() * mutation;
		if (p < -1) p = -1;
		if (p > 1) p = 1;
		pFiber = grassFunction(p);
		pBlood = bloodFunction(-p);
	}
	
	public static void setMAX_G(float mAX_G) {
		System.out.println("Setting MAX_G to " + mAX_G);
		MAX_G = mAX_G;
	}

	public static void setMAX_B(float mAX_B) {
		System.out.println("Setting MAX_B to " + mAX_B);
		MAX_B = mAX_B;
	}

	private static float MAX_G = 0.4f;
	private float grassFunction(float p2) {
		return (float) (a(MAX_G)*p2*p2 + b(MAX_G) * p2 + c(MAX_G));
	}
	private static float MAX_B = 3f;
	private float bloodFunction(float p2) {
		return (float) (a(getMAX_B())*p2*p2 + b(getMAX_B()) * p2 + c(getMAX_B()));
	}
	
	private static float a(float max) {
		return max/4;
	}
	private static float b(float max) {
		return max/2;
	}
	private static float c(float max) {
		return max/4;
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
			blood = blood * MAX_FULLNESS/total;
			fat = fat * MAX_FULLNESS/total;
		}
	}

	/**
	 * Digests
	 */
	private static final float DIGEST_AMOUNT = 1f; //TODO: styr upp konstanter som denna.
	private void digest() {
		float totalFullness = fiber + blood;
		if (totalFullness > DIGEST_AMOUNT ) {
			
			fat += pFiber * fiber * DIGEST_AMOUNT / totalFullness;
			fat += pBlood * blood * DIGEST_AMOUNT / totalFullness;
			
			fiber -= fiber * DIGEST_AMOUNT / totalFullness;
			blood -= blood * DIGEST_AMOUNT / totalFullness;
			
		}
		else {
			fat += pFiber * fiber;
			fat += pBlood * blood;
			fiber = 0;
			blood = 0;
		}
		
	}
	public final static float FAT_TO_ENERGY_FACTOR = 0.02f;
	private void burnFat() {
		fat -= energyCost*FAT_TO_ENERGY_FACTOR;
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

	public void addBlood(float amount) {
		blood += amount;
	}
	public void addFiber(float amount) {
		fiber += amount;
	}

	public float getRelativeFullness() {
		return getMass() / MAX_FULLNESS;
	}

	public boolean canHaveBaby(float birthHungerCost) {
		return (fat / FAT_TO_ENERGY_FACTOR) > birthHungerCost;
	}

	static final float minSpeed = 0.5f;
	private static final float energyCostAtMaxSpeed = 5f;
	public void addRecoverCost(float speed) {
		float c = energyCostAtMaxSpeed / (-1 + 1/minSpeed);
		float b = -2 * c;
		float a = c / minSpeed;
		energyCost += a*speed*speed + b*speed + c;
	}

	public static float getMAX_B() {
		return MAX_B;
	}

}

