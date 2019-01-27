package display.hex;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import constants.Constants;
import display.Circle;
import main.Main;

public class TreeRenderer {
	
	private class TreeTrunkRenderer extends TubeRenderer {
		public TreeTrunkRenderer() {
			super(Constants.Colors.TREE, Constants.Colors.DARK_RED, 6, true, 10, false, false);
		}
	}
	
	private class TreeTopRenderer extends TubeRenderer {
		public TreeTopRenderer() {
			super(Constants.Colors.GRASS, Constants.Colors.TREE_TOP, 6, true, 10, false, false);
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

		trunkRenderer.setColor(trunkRenderer.minColor, trunkRenderer.maxColor, 1, 1);
		trunkRenderer.renderTube(pos, treeTrunkHeight, treeTrunkWidth, 0);

		float yStart = treeTrunkHeight/6;
		float health = Main.mSimulation.mWorld.grass.getHealth(x, z);
		if (health > 0.05f) {
			float treeTopHeight = treeTrunkHeight;//height * (health*0.3f + 0.7f) * scale /2 + 0.2f;
			float treeTopWidth = health * scale * 5 + 0.2f;
			topRenderer.setColor(topRenderer.minColor, Constants.Colors.BLACK, 0.9f, 1);
			topRenderer.renderTube(pos, treeTopHeight, treeTopWidth, yStart);
		}

	}
	
	private float getColorGrad(float y, float yMax) {
		
		return Float.min((y)/yMax, 1);
	}
	
	private void setColorBasedOnHeight(float[] c, float y, float yMax, float alpha) {
		glColor4f(c[0] * getColorGrad(y, yMax), c[1] * getColorGrad(y, yMax), c[2] * getColorGrad(y, yMax), 1f - alpha);
	}
}
