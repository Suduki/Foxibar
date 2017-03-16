package agents;

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

	public void inherit(Species skill) {
		this.speciesId = skill.speciesId;
		
		this.grassHarvest = skill.grassHarvest;
		this.grassDigestion = skill.grassDigestion;
		
		this.bloodDigestion = skill.bloodDigestion;
		this.bloodHarvest = skill.bloodHarvest;
		
		this.speed = skill.speed;
		this.fight = skill.fight;
	}
}