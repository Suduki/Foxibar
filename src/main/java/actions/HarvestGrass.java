package actions;

import talents.Talents;
import world.World;
import agents.Stomach;

public class HarvestGrass extends Harvest {
	public HarvestGrass(World world) {
		super(world.grass, Talents.DIGEST_GRASS, Stomach.FIBER);
	}
}
