package agents;

import constants.Constants;

public class Species {
	
	public static final int BIRTH_HUNGER_COST = 500;
	
	public int speciesId;
	public static int numSpecies = 0;
	public int numAlive;
	
	public float[] color, secondaryColor;
	
	public Brain bestBrain;
	public float bestScore;
	public boolean timeToSave;
	public float p;
	
	public Species(float[] color, float[] secondaryColor) {
		this.color = color;
		this.secondaryColor = secondaryColor;
		speciesId = numSpecies++;
		bestBrain = new Brain(true);
		if (speciesId == 0) {p = -1;}
		else if (speciesId == 1) {p = 1;}
		else {p = Constants.RANDOM.nextFloat()*2f - 1f;}
		
	}
	public float getUglySpeciesFactor() {
		if (numAlive > 500) {
			return 1f;
		}
		else if (numAlive > 1000) {
			return 20f;
		}
		return 1f;
	}
	
	
	public void someoneWasBorn() {
		numAlive++;
	}
	
	public void someoneDied(Animal a) {
		if (a.score > bestScore) {
			bestScore = a.score;
			bestBrain.inherit(a.brain, a.brain);
			timeToSave = true;
		}
		numAlive--;
	}
	
}
