package display;

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
import main.Main;

public class GrassRenderer {
	private boolean drawGrass = true;
	private int grassQuality = 3;

	void drawGrass(float heightScale) {
		if (!drawGrass) return;
		float x0 = -Main.mSimulation.WORLD_SIZE_X/2.0f;
		float z0 = -Main.mSimulation.WORLD_SIZE_Y/2.0f;

		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;

		glLineWidth(5);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		for (int x = 0; x < Main.mSimulation.WORLD_SIZE_X; ++x) {
			for (int z = 0; z < Main.mSimulation.WORLD_SIZE_Y; ++z) {
				float height  = Main.mSimulation.mWorld.grass.height[x][z];
				float xScale = (float)(Math.sqrt(3)*0.5);
				float zScale = 1.5f;

				int hexX = x/2;
				int hexZ = z/2; 

				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;

				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);


				if (!Main.mSimulation.mWorld.grass.tree.isAlive[x][z]) {
					if (height > 0.2f) {
						renderGrassAt(height*2, xpos, zpos, x, z, heightScale);
					}
				}
			}
		}
		glEnd();


		//TODO: Make code pretty (koden efteråt är retired copy/paste.)
		glBegin(GL_QUADS);
		for (int z = 0; z < Main.mSimulation.WORLD_SIZE_Y; ++z) {
			for (int x = 0; x < Main.mSimulation.WORLD_SIZE_X; x+=1) {
				float height  = Main.mSimulation.mWorld.grass.height[x][z];
				float xScale = (float)(Math.sqrt(3)*0.5);
				float zScale = 1.5f;

				int hexX = x/2;
				int hexZ = z/2; 

				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;

				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);


				if (Main.mSimulation.mWorld.grass.tree.isAlive[x][z]) {
					renderTreeTopAt(Main.mSimulation.mWorld.grass.tree.height[x][z], xpos, zpos, x, z, heightScale);
				}
			}
		}
		glEnd();

	}

	void drawAgents(float heightScale) {
		float x0 = -Main.mSimulation.WORLD_SIZE_X/2.0f;
		float z0 = -Main.mSimulation.WORLD_SIZE_Y/2.0f;

		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;

		float xScale = (float)(Math.sqrt(3)*0.5);
		float zScale = 1.5f;
		for (AgentManager<?> manager : Main.mSimulation.agentManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Agent a = manager.alive.get(i);
				int x = (int) a.pos.x;
				int z = (int) a.pos.y;
				int hexX = x/2;
				int hexZ = z/2; 
				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;
				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);
				renderAgentAt(a, xpos, zpos, heightScale);
			}
		}
	}

	float scale = 0.7f;
	float[] xVertices = {-scale, -scale, scale, scale, -scale};
	float[] zVertices = {-scale, scale, scale, -scale, -scale};

	void renderAgentAt(Agent a, float x, float z, float heightScale) {
		glLineWidth(10);
		glBegin(GL_TRIANGLES);
		glColor3f(0,0,0);

		float[] c2 = a.secondaryColor;
		float[] c = a.color;
		float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[(int)a.pos.x][(int)a.pos.y], 1.5);
		h *= heightScale;
		glColor3f(c[0],c[1],c[2]);

		float sizeScale = 0.7f;
		float size = sizeScale + (1f - sizeScale) * a.size;

		float height = size * 3;
		float width = size / 2;

		// Render Side
		for (int i = 0; i < 4; ++i) {
			glColor3f(c2[0],c2[1],c2[2]);
			glVertex3f(x + width * xVertices[i+1], h + height, z + width * zVertices[i+1]);
			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);
			glColor3f(c[0],c[1],c[2]);
			glVertex3f(x,h,z);
		}

		// Render Top
		glColor3f(c2[0],c2[1],c2[2]);
		glVertex3f(x + width * xVertices[2], h + height, z + width * zVertices[2]);
		glVertex3f(x + width * xVertices[0], h + height, z + width * zVertices[0]);
		glVertex3f(x + width * xVertices[1], h + height, z + width * zVertices[1]);

		glVertex3f(x + width * xVertices[0], h + height, z + width * zVertices[0]);
		glVertex3f(x + width * xVertices[2], h + height, z + width * zVertices[2]);
		glVertex3f(x + width * xVertices[3], h + height, z + width * zVertices[3]);
		glEnd();


		glLineWidth(1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		glColor3f(0,0,0); 

		// Render Side
		for (int i = 0; i < 4; ++i) {
			glVertex3f(x,h,z);
			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);


			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);
			glVertex3f(x + width * xVertices[i+1], h + height, z + width * zVertices[i+1]);
		}
		glEnd();
	}

	private void renderTreeAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float[] c = Constants.Colors.TREE;
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		int numSplits = 20;
		numSplits = (int) Math.ceil(numSplits*height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		for (int i = 0; i < numSplits/2; ++i) {
			float colorGrad = 0.5f+(0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.1f*((float)i)/numSplits;
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			glVertex3f(xPix + drawPos.x,y + drawPos.y,zPix + drawPos.z);
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 50f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			glVertex3f(xPix + drawPos.x,y + drawPos.y,zPix + drawPos.z);
		}
	}

	float[] treeVerticesX = new float[5];
	float[] treeVerticesZ = new float[5];
	
	private void renderTreeTopAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float[] c = Constants.Colors.TREE;
		glColor3f(c[0],c[1],c[2]);
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;

		float scale = 0.3f;
		float treeTrunkHeight = scale * (0.5f + (1f-0.5f)*height);
		float treeTrunkWidth = scale;
		if (height < 1f) {
			treeTrunkWidth = scale / 2;
		}
		
		for (int i = 0; i < 5; ++i) {
			treeVerticesX[i] = treeTrunkWidth * xVertices[i];
			treeVerticesZ[i] = treeTrunkWidth * zVertices[i];
		}
		
		
		for (int i = 0; i < 4; ++i) {
			glVertex3f(xPix + treeVerticesX[i], y, zPix + treeVerticesZ[i]);
			glVertex3f(xPix + treeVerticesX[i+1], y, zPix + treeVerticesZ[i+1]);
			glVertex3f(xPix + treeVerticesX[i+1], y+treeTrunkHeight+0.1f, zPix + treeVerticesZ[i+1]);
			glVertex3f(xPix + treeVerticesX[i], y+treeTrunkHeight+0.1f, zPix + treeVerticesZ[i]);
		}
		
		renderTreeTop(height, xPix, zPix, y, treeTrunkHeight);
	}

	private void renderTreeTop(float height, float xPix, float zPix, float y,
			float treeTrunkHeight) {
		float[] c;
		c = Constants.Colors.TREE_TOP;
		glColor3f(c[0],c[1],c[2]);
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);
		
		int numSplits = (int) height + 2;
		float treeTopHeightFactor = 2;
		float widthScale = 1f;
		float treeTopWidthFactor = 0.5f * (widthScale  + (1f - widthScale) * treeTrunkHeight);
		
		float currentY = y+treeTrunkHeight-treeTrunkHeight * treeTopHeightFactor / numSplits;
		
		
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		
		for (int h = 0; h < numSplits; ++h) {
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 50f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			
			float colorGrad = 0.5f+(0.5f*((float)h+1f)/numSplits);
			float alphaGrad = 0.2f*((float)h)/numSplits;
				glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			
			for (int i = 0; i < 5; ++i) {
				treeVerticesX[i] = drawPos.x + treeTopWidthFactor * xVertices[i] * (h * (numSplits - h))/numSplits;
				treeVerticesZ[i] = drawPos.z + treeTopWidthFactor * zVertices[i] * (h * (numSplits - h))/numSplits;
			}
			float nextY = currentY + treeTrunkHeight * treeTopHeightFactor / numSplits;
			
			// Bottom
			glVertex3f(xPix+treeVerticesX[3], currentY, zPix+treeVerticesZ[3]);
			glVertex3f(xPix+treeVerticesX[2], currentY, zPix+treeVerticesZ[2]);
			glVertex3f(xPix+treeVerticesX[1], currentY, zPix+treeVerticesZ[1]);
			glVertex3f(xPix+treeVerticesX[0], currentY, zPix+treeVerticesZ[0]);
			
			
			// Side
			for (int i = 0; i < 4; ++i) {
				glVertex3f(xPix+treeVerticesX[i], currentY, zPix+treeVerticesZ[i]);
				glVertex3f(xPix+treeVerticesX[i+1], currentY, zPix+treeVerticesZ[i+1]);
				glVertex3f(xPix+treeVerticesX[i+1], nextY, zPix+treeVerticesZ[i+1]);
				glVertex3f(xPix+treeVerticesX[i], nextY, zPix+treeVerticesZ[i]);
			}
			
			// Top
			glVertex3f(xPix+treeVerticesX[0], nextY, zPix+treeVerticesZ[0]);
			glVertex3f(xPix+treeVerticesX[1], nextY, zPix+treeVerticesZ[1]);
			glVertex3f(xPix+treeVerticesX[2], nextY, zPix+treeVerticesZ[2]);
			glVertex3f(xPix+treeVerticesX[3], nextY, zPix+treeVerticesZ[3]);
			currentY = nextY;
		}
		
		
	}

	private void renderGrassAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float[] c = Constants.Colors.GRASS_STRAW;
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		int numSplits = grassQuality;
		numSplits = (int) Math.ceil(numSplits*height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		for (int i = 0; i < numSplits; ++i) {
			float colorGrad = 1f - (0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.4f*((float)i)/numSplits;
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			glVertex3f(xPix + drawPos.x,y + drawPos.y,zPix + drawPos.z);
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 4f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			glVertex3f(xPix + drawPos.x,y + drawPos.y,zPix + drawPos.z);
		}

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
