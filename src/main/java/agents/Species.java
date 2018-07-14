package agents;

import constants.Constants;

public class Species {
	
	public static final int BIRTH_HUNGER_COST = 50;
	
	public int speciesId;
	public static int numSpecies = 0;
	public int numAlive;
	
	public float[] color, secondaryColor;
	
	public Brain bestBrain;
	public float bestScore;
	public boolean timeToSave;
	public float p;

	public float fightSkill;
	
	public Species(float[] color, float[] secondaryColor) {
		this.color = color;
		this.secondaryColor = secondaryColor;
		speciesId = numSpecies++;
		bestBrain = new Brain(true);
		if (speciesId == 0) {
			p = -1;
			fightSkill = 0;
		}
		else if (speciesId == 1) {
			p = 1;
			fightSkill = 0;
		}
		else {
			fightSkill = Constants.RANDOM.nextFloat();
			p = fightSkill*2f - 1f;
			System.err.println("Totalgissning här.");
		}
		
	}
	public float getUglySpeciesFactor() {
		if (numAlive > 100) {
			return 200f;
		}
		else if (numAlive > 1000) {
			return 2000f;
		}
		return 1f;
	}
	
	
	public void someoneWasBorn() {
		numAlive++;
	}
	
	public void someoneDied(Animal a) {
		if (a.score >= bestScore && a.score > 5) {
			System.out.println("new best species of id " + speciesId);
			bestScore = a.score;
			bestBrain.inherit(a.brain, a.brain);
		}
		numAlive--;
	}
	
}
