package buttons;


import math.Vector2f;
import world.World;

public class RegenerateWorld extends Button {

	public RegenerateWorld(Vector2f position) {
		super(position);
	}

	@Override
	public void onClick() {
		World.regenerate();
	}

}
