package messages;

import constants.Constants;
import display.DisplayHandler;

public class ToggleRenderAnimals extends Message {

	@Override
	public String messageName() {
		return "ToggleRenderAnimals";
	}
	
	@Override
	public void evaluate(DisplayHandler pDisplayHandler)
	{
		Constants.RENDER_ANIMALS = !Constants.RENDER_ANIMALS;
	}

}
