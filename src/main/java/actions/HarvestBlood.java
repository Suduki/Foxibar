package actions;

import talents.Talents;
import world.World;
import agents.Animal;
import agents.Stomach;

public class HarvestBlood extends HarvestFromGround {
	public HarvestBlood(World world) {
		super(world.blood, Talents.DIGEST_BLOOD, Stomach.BLOOD, Animal.HARVEST_BLOOD);
	}
}
