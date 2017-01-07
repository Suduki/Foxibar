package buttons;

import org.lwjgl.util.vector.Vector2f;

import constants.Constants;

public class ButtonRenderAnimals extends Button {

	public ButtonRenderAnimals(Vector2f position) {
		super(position);
	}

	@Override
	public void onClick() {
		Constants.RENDER_ANIMALS = !Constants.RENDER_ANIMALS;
	}

}
