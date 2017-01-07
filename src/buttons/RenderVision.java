package buttons;

import org.lwjgl.util.vector.Vector2f;

import constants.Constants;

public class RenderVision extends Button {

	public RenderVision(Vector2f position) {
		super(position);
	}

	@Override
	public void onClick() {
		Constants.RENDER_TERRAIN = !Constants.RENDER_TERRAIN;
	}

}
