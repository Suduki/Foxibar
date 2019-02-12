package display.hex;

import static org.lwjgl.opengl.GL11.glColor4f;

import org.joml.Vector3f;

import constants.Constants;
import main.Main;
import plant.Plant;

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
	

	void renderTreeAt(Plant plant, Vector3f pos, float x, float z) {

		float scale = 0.5f;
		float treeTrunkHeight = 8 * scale * plant.size;
		float treeTrunkWidth = scale/2;
		if (plant.size < 0.1f) {
			treeTrunkWidth = scale / 4;
		}

		trunkRenderer.setColor(Constants.Colors.FIBER, Constants.Colors.TREE, 1, 1);
		trunkRenderer.renderTube(pos, treeTrunkHeight, treeTrunkWidth, 0);

		float yStart = treeTrunkHeight/6;
		float health = plant.health;
		if (health > 0.05f) {
			float treeTopHeight = treeTrunkHeight;//plant.size * (health*0.3f + 0.7f) * scale /2 + 0.2f;
			float treeTopWidth = 2 * health * scale * plant.size + 0.1f;
			topRenderer.setColor(topRenderer.minColor, Constants.Colors.BLACK, 0.9f, 1);
			topRenderer.renderTube(pos, treeTopHeight, treeTopWidth, yStart);
		}

	}
	
	private float getColorGrad(float y, float yMax) {
		
		return Float.min((y)/yMax, 1);
	}
}
