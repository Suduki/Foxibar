package main.java.messages;

import main.java.display.DisplayHandler;
import main.java.display.RenderState;

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
