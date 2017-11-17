package messages;

import constants.Constants;
import display.DisplayHandler;
import display.RenderState;

public class ToggleRenderAnimals extends Message {

	@Override
	public String messageName() {
		return "ToggleRenderAnimals";
	}
	
	@Override
	public void evaluate(DisplayHandler pDisplayHandler)
	{
		RenderState.stepState();
	}

}
