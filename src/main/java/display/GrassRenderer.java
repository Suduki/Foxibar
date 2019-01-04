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
	private int grassQuality = 2;
	
	public GrassRenderer() {
		super();
		initCircle();
	}

	void drawGrass(float heightScale) {
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

				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;

				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);


				if (!Main.mSimulation.mWorld.grass.tree.isAlive[x][z]) {
					if (height > 0.2f) {
						renderGrassAt(height, xpos, zpos, x, z, heightScale);
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
					renderTreeAt(Main.mSimulation.mWorld.grass.tree.height[x][z], xpos, zpos, x, z, heightScale);
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
				if (a == null) break;
				int x = (int) a.pos.x;
				int z = (int) a.pos.y;
				int hexX = x/2;
				int hexZ = z/2; 
				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;
				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);
				
				float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
				renderAgentAt(a, h, xpos, zpos, heightScale);
			}
		}
	}

	float scale = 0.7f;
	int numTreeCircularVertices = 6;
	
	float[] xVertices;
	float[] zVertices;

	float[] treeVerticesX = new float[numTreeCircularVertices+1];
	float[] treeVerticesZ = new float[numTreeCircularVertices+1];

	private void initCircle() {
		if (xVertices != null) return;
		
		xVertices = new float[numTreeCircularVertices+1];
		zVertices = new float[numTreeCircularVertices+1];
		float angle = 0;
		for (int i = 0; i < numTreeCircularVertices; ++i) {
			angle += Math.PI*2 /numTreeCircularVertices;
			xVertices[i] = (float)Math.cos(angle);
			zVertices[i] = (float)Math.sin(angle);
		}
		xVertices[numTreeCircularVertices] = xVertices[0];
		zVertices[numTreeCircularVertices] = zVertices[0];
	}
	
	void renderAgentAt(Agent a, float h, float x, float z, float heightScale) {

		float[] c2 = a.secondaryColor;
		float[] c = a.color;
		h *= heightScale;

		float sizeScale = 0.7f;
		float size = sizeScale + (1f - sizeScale) * a.size;

		float height = size * 3;
		float width = size / 2;

		glBegin(GL_TRIANGLES);
		glColor3f(c[0],c[1],c[2]);
		
		// Render Side
		for (int i = 0; i < xVertices.length-1; ++i) {
			glColor3f(c2[0],c2[1],c2[2]);
			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);
			glVertex3f(x + width * xVertices[i+1], h + height, z + width * zVertices[i+1]);
			glColor3f(c[0],c[1],c[2]);
			glVertex3f(x,h,z);
		}

		// Render Top
		glColor3f(c2[0],c2[1],c2[2]);
		for (int i = 0; i < xVertices.length-1; ++i) {
			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);
			glVertex3f(x, h + height, z);
			glVertex3f(x + width * xVertices[i+1], h + height, z + width * zVertices[i+1]);
		}

		glEnd();


		glLineWidth(1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		glColor3f(0,0,0); 

		for (int i = 0; i < xVertices.length-1; ++i) {
			glVertex3f(x,h,z);
			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);


			glVertex3f(x + width * xVertices[i], h + height, z + width * zVertices[i]);
			glVertex3f(x + width * xVertices[i+1], h + height, z + width * zVertices[i+1]);
		}
		glEnd();
	}

	private void renderTreeAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
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
		
		for (int i = 0; i < treeVerticesX.length; ++i) {
			treeVerticesX[i] = treeTrunkWidth * xVertices[i];
			treeVerticesZ[i] = treeTrunkWidth * zVertices[i];
		}
		
		
		for (int i = 0; i < treeVerticesX.length-1; ++i) {
			glVertex3f(xPix + treeVerticesX[i+1], y+treeTrunkHeight+0.1f, zPix + treeVerticesZ[i+1]);
			glVertex3f(xPix + treeVerticesX[i+1], y, zPix + treeVerticesZ[i+1]);
			glVertex3f(xPix + treeVerticesX[i], y, zPix + treeVerticesZ[i]);
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
		
		float oldMiddleX = xPix;
		float oldMiddleZ = zPix;
		
		for (int h = 0; h < numSplits; ++h) {
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 70f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			
			float colorGrad = 0.5f+(0.5f*((float)h+1f)/numSplits);
			float alphaGrad = 0.2f*((float)h)/numSplits;
				glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			
			for (int i = 0; i < treeVerticesX.length; ++i) {
				treeVerticesX[i] = drawPos.x + treeTopWidthFactor * xVertices[i] * (h * (numSplits - h))/numSplits;
				treeVerticesZ[i] = drawPos.z + treeTopWidthFactor * zVertices[i] * (h * (numSplits - h))/numSplits;
			}
			float nextY = currentY + treeTrunkHeight * treeTopHeightFactor / numSplits;
			float nextMiddleX = xPix+drawPos.x;
			float nextMiddleZ = zPix+drawPos.z;
			
			renderTreeTopBottom(xPix, zPix, currentY, nextMiddleX, nextMiddleZ);
			renderTreeTopTop(xPix, zPix, oldMiddleX, oldMiddleZ, nextY);
			renderTreeTopSide(xPix, zPix, currentY, nextY);
			
			currentY = nextY;
			oldMiddleX = nextMiddleX;
			oldMiddleZ = nextMiddleZ;
		}
		
		
	}

	private void renderTreeTopBottom(float xPix, float zPix, float currentY, float nextMiddleX, float nextMiddleZ) {
		for (int i = 0; i < treeVerticesX.length-1; ++i) {
			glVertex3f(xPix+treeVerticesX[i+1], currentY, zPix+treeVerticesZ[i+1]);
			glVertex3f(nextMiddleX, currentY, nextMiddleZ);
			glVertex3f(nextMiddleX, currentY, nextMiddleZ);
			glVertex3f(xPix+treeVerticesX[i], currentY, zPix+treeVerticesZ[i]);
		}
	}

	private void renderTreeTopTop(float xPix, float zPix, float oldMiddleX, float oldMiddleZ, float nextY) {
		for (int i = 0; i < treeVerticesX.length-1; ++i) {
			glVertex3f(xPix+treeVerticesX[i], nextY, zPix+treeVerticesZ[i]);
			glVertex3f(oldMiddleX, nextY, oldMiddleZ);
			glVertex3f(oldMiddleX, nextY, oldMiddleZ);
			glVertex3f(xPix+treeVerticesX[i+1], nextY, zPix+treeVerticesZ[i+1]);
		}
	}

	private void renderTreeTopSide(float xPix, float zPix, float currentY, float nextY) {
		for (int i = 0; i < treeVerticesX.length-1; ++i) {
			glVertex3f(xPix+treeVerticesX[i+1], currentY, zPix+treeVerticesZ[i+1]);
			glVertex3f(xPix+treeVerticesX[i], currentY, zPix+treeVerticesZ[i]);
			glVertex3f(xPix+treeVerticesX[i], nextY, zPix+treeVerticesZ[i]);
			glVertex3f(xPix+treeVerticesX[i+1], nextY, zPix+treeVerticesZ[i+1]);
		}
	}

	private void renderGrassAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float[] c = Constants.Colors.GRASS_STRAW;
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		int numSplits = grassQuality + 1;
		numSplits = (int) Math.ceil(numSplits*height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		
		float nextX = xPix;
		float nextY = y;
		float nextZ = zPix;
		
		
		for (int i = 0; i < numSplits; ++i) {
			float colorGrad = 1f - (0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.4f*((float)i)/numSplits;
			float currentHalfWidth = (0.2f * (numSplits - i)) / numSplits;
			float nextHalfWidth = (0.2f * (numSplits - (i+1))) / numSplits;
			
			float currentX = nextX;
			float currentY = nextY;
			float currentZ = nextZ;
			
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 4f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			nextX = xPix + drawPos.x;
			nextY = y + drawPos.y;
			nextZ = zPix + drawPos.z;
			
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			
			for (int circleVertice = 0; circleVertice < treeVerticesX.length-1; ++circleVertice) {
				glVertex3f(currentX + currentHalfWidth * treeVerticesX[circleVertice+1], currentY, currentZ + currentHalfWidth * treeVerticesZ[circleVertice+1]);
				glVertex3f(currentX + currentHalfWidth * treeVerticesX[circleVertice], currentY, currentZ + currentHalfWidth * treeVerticesZ[circleVertice]);
				glVertex3f(nextX + nextHalfWidth * treeVerticesX[circleVertice], nextY, nextZ + nextHalfWidth * treeVerticesZ[circleVertice]);
				glVertex3f(nextX + nextHalfWidth * treeVerticesX[circleVertice+1], nextY, nextZ + nextHalfWidth * treeVerticesZ[circleVertice+1]);
			}
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
