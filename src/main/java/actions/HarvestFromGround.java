package actions;

import org.joml.Vector2f;

import talents.Talents;
import world.TileElement;
import agents.Animal;

public class HarvestFromGround extends Action {
	public Vector2f dir;
	public float heightBelowAgent;
	public float heightNearby;
	
	private boolean searchPossible;
	
	private TileElement stuffToHarvest;
	private final int DIGEST_SKILL;
	private final int STOMACH_ID;
	private final float HARVEST_SKILL;
	

	public HarvestFromGround(TileElement stuffToHarvest, final int DIGEST_SKILL, final int STOMACH_ID, final float HARVEST_SKILL) {
		super();
		dir = new Vector2f();
		this.DIGEST_SKILL = DIGEST_SKILL;
		this.STOMACH_ID = STOMACH_ID;
		this.HARVEST_SKILL = HARVEST_SKILL;
		this.stuffToHarvest = stuffToHarvest;
	}

	@Override
	public boolean determineIfPossible(Animal a) {
		
		searchPossible = canSearch(a);
		
		isPossible = a.talents.getRelative(DIGEST_SKILL) > 0.2f
				&& !a.stomach.isFull()
				&& (canHarvest(a) || searchPossible);
		
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		float energyLeft = 1f;
		
		energyLeft -= harvest(a);
		if (searchPossible) {
			a.vel.set(dir);
			a.vel.mul(energyLeft);
			a.move();
		}
	}
	
	private boolean canHarvest(Animal a) {
		heightBelowAgent = stuffToHarvest.getHeight((int)a.pos.x, (int)a.pos.y);
		return heightBelowAgent > 0.05f;
	}
	
	private boolean canSearch(Animal a) {
		heightNearby = stuffToHarvest.seekHeight(dir, (int)a.pos.x, (int)a.pos.y);
		return heightNearby > 0.1f;
	}
	
	private float harvest(Animal a) {
		float harvestSkillBasedOnTalent = HARVEST_SKILL * a.talents.getRelative(DIGEST_SKILL);
		return a.stomach.add(STOMACH_ID, stuffToHarvest.harvest(harvestSkillBasedOnTalent, (int) a.pos.x, (int) a.pos.y)) / harvestSkillBasedOnTalent;
	}
}
