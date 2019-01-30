package display.hex;

import constants.Constants;
import main.Main;
import simulation.Simulation;

public class GrassRenderer extends TubeRenderer {
	private boolean drawGrass = true;

	private TreeRenderer mTreeRenderer = null;

	public GrassRenderer() {
		super(Constants.Colors.GRASS_STRAW, Constants.Colors.GRASS, 3, true, 2, false, false, true);
		setColor(Constants.Colors.GRASS_STRAW, Constants.Colors.GRASS, 0.5f, 1);

		mTreeRenderer = new TreeRenderer();
	}

	public void drawGrass(float heightScale) {
		if (!drawGrass)
			return;
		float x0 = -Simulation.WORLD_SIZE_X / 2.0f;
		float z0 = -Simulation.WORLD_SIZE_Y / 2.0f;

		float xNudge = (float) (Math.sqrt(3.0f) * 0.2f);
		float zNudge = 3.0f / 9.0f;

		for (int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int z = 0; z < Simulation.WORLD_SIZE_Y; ++z) {
				float height = Main.mSimulation.mWorld.grass.height[x][z];
				float xScale = (float) (Math.sqrt(3) * 0.5f);
				float zScale = 1.5f;

				int hexX = x / 2;
				int hexZ = z / 2;

				float xpos = x0 + hexX * 2 * xScale + ((x % 2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ * zScale + ((z % 2 == 0) ? -zNudge : zNudge);

				float y = (float) Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5) * heightScale;
				groundPos.set(xpos, y, zpos);

				if (Main.mSimulation.mWorld.grass.tree.isAlive[x][z]) {
					mTreeRenderer.renderTreeAt(Main.mSimulation.mWorld.grass.tree.height[x][z], groundPos, x, z);
				} else {
					if (height > 0.2f) {
						renderTube(groundPos, height, height / 6, 0);
					}
				}
			}
		}
	}

	public void setDrawGrass() {
		drawGrass = !drawGrass;
	}
}
