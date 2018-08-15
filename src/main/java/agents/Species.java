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

	public float fightSkill;
	
	public Species(float[] color, float[] secondaryColor) {
		this.color = color;
		this.secondaryColor = secondaryColor;
		speciesId = numSpecies++;
		bestBrain = new Brain(true);
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
	
	public void someoneDied(Animal agent) {
		if (agent.score >= bestScore && agent.score > 5) {
			System.out.println("new best species of id " + speciesId);
			bestScore = agent.score;
			bestBrain.inherit(agent.brain);
		}
		numAlive--;
	}
	
}
