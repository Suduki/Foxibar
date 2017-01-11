package buttons;


import constants.Constants;
import math.Vector2f;

public class ButtonRenderAnimals extends Button {

	public ButtonRenderAnimals(Vector2f position) {
		super(position);
	}

	@Override
	public void onClick() {
		Constants.RENDER_ANIMALS = !Constants.RENDER_ANIMALS;
	}

}
