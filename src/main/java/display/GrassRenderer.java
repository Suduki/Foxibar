package display;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
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


				if (Main.mSimulation.mWorld.grass.tree.isAlive[x][z]) {
					renderTreeAt(Main.mSimulation.mWorld.grass.tree.height[x][z], xpos, zpos, x, z, heightScale);
				}
				else {
					if (height > 0.2f) {
						renderGrassAt(height*2, xpos, zpos, x, z, heightScale);
					}
				}
			}
		}
		glEnd();
		
		
		//TODO: Make code pretty (koden efteråt är retired copy/paste.)
		glLineWidth(15);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
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
		
		glLineWidth(10);
		glBegin(GL_LINES);
		glColor3f(0,0,0);
		
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
		glEnd();
		glLineWidth(1);
	}
	
	void renderAgentAt(Agent a, float x, float z, float heightScale) {
		float[] c = a.secondaryColor;
		float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[(int)a.pos.x][(int)a.pos.y], 1.5);
		h *= heightScale;
		glColor3f(c[0],c[1],c[2]);
		glVertex3f(x,h,z);
		glVertex3f(x,h+1,z);
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

	private void renderTreeTopAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float[] c = Constants.Colors.TREE;
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);
		
		int numSplits = 4;
		numSplits = (int) Math.ceil(numSplits*height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		
		c = Constants.Colors.TREE_TOP;
		//TODO : Styr upp sån här idiotduplicerad kod
		for (int i = 0; i < numSplits; ++i) {
			float colorGrad = 0.5f+(0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.1f*((float)i)/numSplits;
			if (i > numSplits/2) {
				glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
				glVertex3f(xPix + drawPos.x,y + drawPos.y,zPix + drawPos.z);
				force.y = 20f; // Stiffness, force towards middle TODO: Make a force normal from ground
			}
			else {
				force.y = 50f; // Stiffness, force towards middle TODO: Make a force normal from ground
			}
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			if (i > numSplits/2) {
				glVertex3f(xPix + drawPos.x,y + drawPos.y,zPix + drawPos.z);
			}

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
