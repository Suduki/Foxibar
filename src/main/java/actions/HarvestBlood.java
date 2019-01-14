package actions;

import talents.Talents;
import world.World;
import agents.Stomach;

public class HarvestBlood extends Harvest {
	public HarvestBlood(World world) {
		super(world.blood, Talents.DIGEST_BLOOD, Stomach.BLOOD);
	}
}
