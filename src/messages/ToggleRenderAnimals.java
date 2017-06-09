package messages;

import constants.Constants;
import constants.RenderState;
import display.DisplayHandler;

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
