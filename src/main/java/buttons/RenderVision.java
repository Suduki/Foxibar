package main.java.buttons;


import main.java.display.RenderState;
import main.java.math.Vector2f;

public class RenderVision extends Button {

	public RenderVision(Vector2f position) {
		super(position);
	}

	//@Override
	public void onClick() {
		RenderState.RENDER_TERRAIN = !RenderState.RENDER_TERRAIN;
	}

}
