package display.hex;

import static org.lwjgl.opengl.GL11.glColor4f;

import org.joml.Vector3f;

import constants.Constants;
import main.Main;

public class TreeRenderer {
	
	private class TreeTrunkRenderer extends TubeRenderer {
		public TreeTrunkRenderer() {
			super(Constants.Colors.TREE, Constants.Colors.DARK_RED, 6, true, 10, false, false, true);
		}
	}
	
	private class TreeTopRenderer extends TubeRenderer {
		public TreeTopRenderer() {
			super(Constants.Colors.GRASS, Constants.Colors.TREE_TOP, 6, true, 10, false, false, true);
		}
		
		@Override
		protected float heightToRadius(float h, float tubeMaxRadius) {
			return (h * (1f - h)) * 4 * tubeMaxRadius;
		}
	}
	
	private final TreeTrunkRenderer trunkRenderer = new TreeTrunkRenderer();
	private final TreeTopRenderer topRenderer = new TreeTopRenderer();
	

	void renderTreeAt(float height, Vector3f pos, int x, int z) {

		float scale = 0.2f;
		float treeTrunkHeight = 2* scale * height;
		float treeTrunkWidth = scale;
		if (height < 1f) {
			treeTrunkWidth = scale / 2;
		}

		trunkRenderer.setColor(Constants.Colors.FIBER, Constants.Colors.TREE, 1, 1);
		trunkRenderer.renderTube(pos, treeTrunkHeight, treeTrunkWidth, 0);

		float yStart = treeTrunkHeight/6;
		float health = Main.mSimulation.mWorld.grass.getHealth(x, z);
		if (health > 0.05f) {
			float treeTopHeight = treeTrunkHeight;//height * (health*0.3f + 0.7f) * scale /2 + 0.2f;
			float treeTopWidth = health * scale * height + 0.1f;
			topRenderer.setColor(topRenderer.minColor, Constants.Colors.BLACK, 0.9f, 1);
			topRenderer.renderTube(pos, treeTopHeight, treeTopWidth, yStart);
		}

	}
	
	private float getColorGrad(float y, float yMax) {
		
		return Float.min((y)/yMax, 1);
	}
}
