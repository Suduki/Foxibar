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
	
	public Decision decision;
	
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
		
		switch(speciesId) {
			case Constants.SpeciesId.GRASSLER:
				decision = new Decision(Decision.STANDARD_GRASSLER);
				break;
			case Constants.SpeciesId.BLOODLING:
				decision = new Decision(Decision.STANDARD_BLOODLING);
				break;
			default:
				System.err.println("What is this species?" + speciesId);
		}
	}
	
	public Species() {
		decision = new Decision(Decision.STANDARD_GRASSLER);
	}

	public void inherit(Species mom, Species dad) {
		this.speciesId = mom.speciesId; // Mom and dad are the same here.
		
		float evolution = 0.0f; // Needs balance otherwise. Evolving this is advanced stuff.
		this.grassHarvest = (mom.grassHarvest + dad.grassHarvest)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		this.grassDigestion = (mom.grassDigestion + dad.grassDigestion)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		
		this.bloodHarvest = (mom.bloodHarvest + dad.bloodHarvest)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		this.bloodDigestion = (mom.bloodDigestion + dad.bloodDigestion)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		
		this.speed = (mom.speed + dad.speed)/2 + evolution*(Constants.RANDOM.nextFloat()-0.5f);
		this.fight = (mom.fight + dad.fight)/2;
		
		this.decision.inherit(mom.decision, dad.decision);
	}
}