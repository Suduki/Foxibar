package agents;

public class Skill {
	int speciesId;
	
	float grassHarvest;
	float grassDigestion;
	
	float bloodDigestion;
	float bloodHarvest;

	float speed;
	float fight;
	
	
	public Skill(int speciesId, float grassHarvest, float grassDigestion,
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
	
	public Skill() {
	}

	public void inherit(Skill skill) {
		this.grassHarvest = skill.grassHarvest;
		this.grassDigestion = skill.grassDigestion;
		
		this.bloodDigestion = skill.bloodDigestion;
		this.bloodHarvest = skill.bloodHarvest;
		
		this.speed = skill.speed;
		this.fight = skill.fight;
	}
}