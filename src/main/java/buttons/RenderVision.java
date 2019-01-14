package buttons;


import org.joml.Vector2f;

import display.RenderState;

public class RenderVision extends Button {

	public RenderVision(Vector2f position) {
		super(position);
	}

	//@Override
	public void onClick() {
		RenderState.RENDER_TERRAIN = !RenderState.RENDER_TERRAIN;
	}

}
