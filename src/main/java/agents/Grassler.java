package agents;

import actions.Action;
import constants.Constants;
import talents.Talents;
import vision.Vision;
import world.World;

public class Grassler extends Animal {

	public Grassler(World world) {
		super(world);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}
	
	@Override
	protected void actionUpdate() {
		
		Action action = Action.harvestGrass;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		
		action = Action.randomWalk;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		System.err.println("Should always be able to commit to an action");
		return;
	}
	
	@Override
	public boolean isCloselyRelatedTo(Animal a) {
		return isSameClassAs(a);
	}
	
	private static final int[] presetSkills = {Talents.DIGEST_GRASS, Talents.MATE_COST}; 
	@Override
	protected void inherit(Animal a) {
		super.inherit(a);
		
		if (a == null) {
			talents.improveSkill(presetSkills);
		}
	}
}
