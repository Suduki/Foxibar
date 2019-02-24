package actions;

import talents.Talents;
import world.World;
import agents.Animal;
import agents.Stomach;

public class HarvestGrass extends HarvestFromGround {
	public HarvestGrass(World world) {
		super(world.grass, Talents.DIGEST_GRASS, Stomach.GRASS, Animal.HARVEST_GRASS);
	}
}
