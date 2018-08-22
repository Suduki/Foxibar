package agents;

import java.awt.List;
import java.util.ArrayList;

import constants.Constants;

public class Species {
	
	public static ArrayList<Species> speciesList;
	
	public int speciesId;
	public int numAlive;
	
	public float[] color, secondaryColor;
	
	public Brain bestBrain;
	public float bestScore;
	public boolean timeToSave;

	public float fightSkill;
	
	public Species(float[] color, float[] secondaryColor) {
		if (speciesList == null) {speciesList = new ArrayList<Species>();}
		speciesList.add(this);
		
		this.color = color;
		this.secondaryColor = secondaryColor;
		speciesId = speciesList.size();
		bestBrain = new Brain(true);
		fightSkill = 1;
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
	public static Species getSpeciesFromId(int id) {
		return speciesList.get(id);
	}
	
}
