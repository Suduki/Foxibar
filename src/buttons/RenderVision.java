package buttons;


import constants.Constants;
import math.Vector2f;

public class RenderVision extends Button {

	public RenderVision(Vector2f position) {
		super(position);
	}

	//@Override
	public void onClick() {
		Constants.RENDER_TERRAIN = !Constants.RENDER_TERRAIN;
	}

}
