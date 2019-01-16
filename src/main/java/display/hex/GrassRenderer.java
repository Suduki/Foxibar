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

public class GrassRenderer {
	private boolean drawGrass = true;
	private int grassQuality = 1;
	
	private Circle circle;
	private TreeRenderer mTreeRenderer = null;

	public GrassRenderer() {
		super();
		
		circle = new Circle(3, 1, null);
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
		float[] c = Constants.Colors.GRASS_STRAW;
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5) * heightScale;
		
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		int numSplits = (int) Math.ceil((grassQuality + 1)*height);
		
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();

		float nextX = xPix;
		float nextY = y;
		float nextZ = zPix;

		float currentX = nextX;
		float currentY = nextY;
		float currentZ = nextZ;

		for (int i = 0; i < numSplits; ++i) {
			float colorGrad = getColorGrad(numSplits, i);
			float alphaGrad = getAlphaGrad(numSplits, i);
			float nextColorGrad = getColorGrad(numSplits, i+1);
			float nextAlphaGrad = getAlphaGrad(numSplits, i+1);
			
			float grassWidth = 0.03f;
			
			
			float currentRadius = (grassWidth * (numSplits - i)) / numSplits;
			float nextRadius = (grassWidth * (numSplits - (i+1))) / numSplits;

			currentX = nextX;
			currentY = nextY;
			currentZ = nextZ;

			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 4f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			nextX = xPix + drawPos.x;
			nextY = y + drawPos.y;
			nextZ = zPix + drawPos.z;

			int circleVertice;
			for (circleVertice = 0; circleVertice < circle.vertices.length; ++circleVertice) {
				glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
				glVertex3f(currentX + circle.getScaledXAt(circleVertice+1, currentRadius), currentY, currentZ + circle.getScaledZAt(circleVertice+1, currentRadius));
				glVertex3f(currentX + circle.getScaledXAt(circleVertice, currentRadius), currentY, currentZ + circle.getScaledZAt(circleVertice, currentRadius));
				
				glColor4f(c[0]*nextColorGrad,c[1]*nextColorGrad,c[2]*nextColorGrad, 1f - nextAlphaGrad);
				glVertex3f(nextX + circle.getScaledXAt(circleVertice, nextRadius), nextY, nextZ + circle.getScaledZAt(circleVertice, nextRadius));
				glVertex3f(nextX + circle.getScaledXAt(circleVertice+1, nextRadius), nextY, nextZ + circle.getScaledZAt(circleVertice+1, nextRadius));
			}
		}
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
