package agents;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runners.JUnit4;

import constants.Constants;
import world.World;

public class Stomach {

	private float energyCost;
	public float fiber;
	public float blood;
	public float fat;
	private float p; // -1 for bloodling, 1 for grassler
	private float pFiber;
	private float pBlood;
	private float uglySpeciesFactor;
	private static final float MUTATION = 0.1f;
	
	public void inherit(Stomach parent) {
		p = (0.5f - Constants.RANDOM.nextFloat()) * MUTATION + parent.p;
		if (p < -1) p = -1;
		if (p > 1) p = 1;
		init(p);
		
		
	}
	
	private void init(float p2) {
		p = p2;
		pFiber = mattiasFunction(p);
		pBlood = mattiasFunction(-p);
	}

	private float a = 0.2f;
	private float b = 1f;
	private float c = 0.2f;
	private float mattiasFunction(float p2) {
		return (float) (a * Math.exp(b*p2) + c);
	}
	@Test
	public void testMattiasFunction() { //TODO: Låt Mattias styra upp detta.....
		System.out.println("Mattias Function Test:" +mattiasFunction(-1) +" " + mattiasFunction(0) + " " + mattiasFunction(1));
		org.junit.Assert.assertTrue(mattiasFunction(-1) < 0.5f);
		org.junit.Assert.assertTrue(mattiasFunction(1) > 0.5f);
		org.junit.Assert.assertTrue(mattiasFunction(0) < 0.6f);
	}

	public void addHealCost(float f) {
		float healCostFactor = 0; // TODO
		energyCost += f*healCostFactor;
	}
	public void addGrowCost(float f) {
		float growCostFactor = 0; // TODO
		energyCost += f*growCostFactor;
	}
	
	/**
	 * Called at the end of round to digest blood/grass and create fat.
	 * Also burns the fat.
	 * @return fat > 0, whether this animal is critically hungry
	 */
	public boolean stepStomach(Species species) {
		uglySpeciesFactor = species.getUglySpeciesFactor();
		energyCost = 1;
		digest();
		burnFat();
		return fat > 0;
	}

	/**
	 * Digests
	 */
	private static final float digestAmount = 1; //TODO: styr upp konstanter som denna.
	private void digest() {
		
		float totalFullness = fiber + blood;
		if (totalFullness > digestAmount ) {
			
			energyCost += pFiber * fiber * uglySpeciesFactor * digestAmount / totalFullness;
			energyCost += pBlood * blood * uglySpeciesFactor * digestAmount / totalFullness;
			
			fiber -= fiber * digestAmount / totalFullness;
			blood -= blood * digestAmount / totalFullness;
			
			fat += digestAmount;
		}
		else {
			energyCost += pFiber * fiber * uglySpeciesFactor;
			energyCost += pBlood * blood * uglySpeciesFactor;
			System.out.println(fiber + " " + blood);
			fiber = 0;
			blood = 0;
			
			fat += totalFullness;
		}
		
	}
	private void burnFat() {
		fat -= energyCost;
		System.out.println(energyCost);
		energyCost = 0;
		World.air.addCarbon(energyCost);
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
	
}

