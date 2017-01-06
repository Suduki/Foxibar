package buttons;

import org.lwjgl.util.vector.Vector2f;

import agents.Animal;
import world.World;

public class ButtonKillAll extends Button {

	public ButtonKillAll(Vector2f position) {
		super(position);
		this.texturePath = "pics/killAllButtonTexture.png";
	}

	@Override
	public void onClick() {
		Animal.killAllAnimals();
		World.grass.killAllGrass();
	}

	

}
