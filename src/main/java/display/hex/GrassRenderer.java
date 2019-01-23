package display.hex;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

import agents.Agent;
import agents.AgentManager;
import agents.Brainler;
import world.Terrain;
import world.World;
import constants.Constants;
import display.Circle;
import main.Main;

public class GrassRenderer extends TubeRenderer {
	private boolean drawGrass = true;
	private int grassQuality = 1;
	
	private TreeRenderer mTreeRenderer = null;

	public GrassRenderer() {
		super(Constants.Colors.GRASS_STRAW, Constants.Colors.GRASS, 3, true, 4, false, false);
		
		mTreeRenderer = new TreeRenderer();
	}

	public void drawGrass(float heightScale) {
		if (!drawGrass) return;
		float x0 = -Main.mSimulation.WORLD_SIZE_X/2.0f;
		float z0 = -Main.mSimulation.WORLD_SIZE_Y/2.0f;

		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_QUADS);
		for (int x = 0; x < Main.mSimulation.WORLD_SIZE_X; ++x) {
			for (int z = 0; z < Main.mSimulation.WORLD_SIZE_Y; ++z) {
				float height  = Main.mSimulation.mWorld.grass.height[x][z];
				float xScale = (float)(Math.sqrt(3)*0.5);
				float zScale = 1.5f;

				int hexX = x/2;
				int hexZ = z/2; 

				float xpos = x0 + hexX*2*xScale + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);


				if (Main.mSimulation.mWorld.grass.tree.isAlive[x][z]) {
					mTreeRenderer.renderTreeAt(Main.mSimulation.mWorld.grass.tree.height[x][z], xpos, zpos, x, z, heightScale);
				}
				else {
					if (height > 0.2f) {
						renderGrassAt(height, xpos, zpos, x, z, heightScale);
					}
				}
			}
		}
		glEnd();
	}

	private void renderGrassAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5) * heightScale;
		
		pos.set(xPix, y, zPix);
		renderTube(pos, height, height/3, 0);
	}

	private float getAlphaGrad(int numSplits, int i) {
		return 0.4f*((float)i)/numSplits;
	}

	private float getColorGrad(int numSplits, int i) {
		return 1f - (0.2f*((float)i+1f)/numSplits);
	}

	public void setDrawGrass() {
		drawGrass = !drawGrass;
	}

	public void stepGrassQuality() {
		grassQuality++;
		if (grassQuality > 8) {
			grassQuality = 1;
		}
	}
}
