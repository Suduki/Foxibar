package buttons;

import agents.Animal;
import math.Vector2f;
import world.World;

public class ButtonKillAll extends Button {

	public ButtonKillAll(Vector2f position) {
		super(position);
		this.texturePath = "pics/killAllButtonTexture.png";
	}

	@Override
	public void onClick() {
		Animal.killAll = true;
		World.grass.killAllGrass();
	}

	

}
