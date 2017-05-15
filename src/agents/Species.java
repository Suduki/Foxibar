package agents;

import constants.Constants;

public class Species {
	public int speciesId;
	
	float grassHarvest;
	float grassDigestion;
	
	float bloodDigestion;
	float bloodHarvest;

	float speed;
	float fight;
	
	public Species(int speciesId, float grassHarvest, float grassDigestion,
			float bloodHarvest, float bloodDigestion, float speed,
			float fight) {
		this.speciesId = speciesId;
		this.grassHarvest = grassHarvest;
		this.grassDigestion = grassDigestion;
		this.bloodDigestion = bloodDigestion;
		this.bloodHarvest = bloodHarvest;
		this.speed = speed;
		this.fight = fight;

	}
	
	public Species() {
	}

	public void inherit(Species mom, Species dad) {
		this.speciesId = mom.speciesId; 
		
		float evolution = 0.0f; // Needs balance otherwise. Evolving this is advanced stuff.
		this.grassHarvest = (mom.grassHarvest + dad.grassHarvest)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		this.grassDigestion = (mom.grassDigestion + dad.grassDigestion)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		
		this.bloodHarvest = (mom.bloodHarvest + dad.bloodHarvest)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		this.bloodDigestion = (mom.bloodDigestion + dad.bloodDigestion)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		
		this.speed = (mom.speed + dad.speed)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		this.fight = (mom.fight + dad.fight)/2;
	}
}