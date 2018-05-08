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
import agents.Animal;
import world.Terrain;
import world.World;
import constants.Constants;

public class GrassRenderer {
	private boolean drawGrass = true;
	private int grassQuality = 3;
	
	void drawGrass(float heightScale) {
		if (!drawGrass) return;
		int i = 0;
		float x0 = -Constants.WORLD_SIZE_X/2.0f;
		float z0 = -Constants.WORLD_SIZE_Y/2.0f;
		
		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;
		
		glLineWidth(5);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
			for (int x = 0; x < Constants.WORLD_SIZE_X; x+=1) {
				float height  = World.grass.height[i];
				float xScale = (float)(Math.sqrt(3)*0.5);
				float zScale = 1.5f;

				int hexX = x/2;
				int hexZ = z/2; 

				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;

				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);


				if (World.grass.tree.isAlive[i]) {
					renderTreeAt(World.grass.tree.height[i], xpos, zpos, i, heightScale);
				}
				else {
					if (height > 0.3f) {
						renderGrassAt(height*2, xpos, zpos, i, heightScale);
					}
				}
				++i;
			}
		}
		glEnd();
		
		
		//TODO: Make code pretty (koden efteråt är retired copy/paste.)
		i = 0;
		glLineWidth(15);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
			for (int x = 0; x < Constants.WORLD_SIZE_X; x+=1) {
				float height  = World.grass.height[i];
				float xScale = (float)(Math.sqrt(3)*0.5);
				float zScale = 1.5f;

				int hexX = x/2;
				int hexZ = z/2; 

				float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;

				float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);


				if (World.grass.tree.isAlive[i]) {
					renderTreeTopAt(World.grass.tree.height[i], xpos, zpos, i, heightScale);
				}

				++i;
			}
		}
		glEnd();
		
	}
	
	void drawAgents(float heightScale) {
		int i = 0;
		float x0 = -Constants.WORLD_SIZE_X/2.0f;
		float z0 = -Constants.WORLD_SIZE_Y/2.0f;
		
		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;
		
		glLineWidth(10);
		glBegin(GL_LINES);
		glColor3f(0,0,0);
		for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
			for (int x = 0; x < Constants.WORLD_SIZE_X; x+=1) {
				// RENDER ANIMAL
				Agent id = World.animalManager.containsAnimals[i];
				if (id != null) {
					float xScale = (float)(Math.sqrt(3)*0.5);
					float zScale = 1.5f;
										
					int hexX = x/2;
					int hexZ = z/2; 
					
					float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;
					
					float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
					float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);
					
					if (id.getClass() == Animal.class) {
						renderAnimalAt((Animal) id, xpos, zpos, heightScale);
					}
				}
				
				++i;
			}
		}
		glEnd();
		glLineWidth(1);
	}
	
	void renderAnimalAt(Animal animal, float x, float z, float heightScale) {
		float[] c = animal.species.secondaryColor;
		float h = (float)Math.pow(World.terrain.height[animal.pos], 1.5);
		h *= heightScale;
		glColor3f(c[0],c[1],c[2]);
		glVertex3f(x,h,z);
		glVertex3f(x,h+1,z);
	}

	private void renderTreeAt(float height, float x, float z, int pos, float heightScale) {
		float[] c = Constants.Colors.TREE;
		float y = (float)Math.pow(World.terrain.height[pos], 1.5);
		y *= heightScale;
		float xWind = 1f-2*World.wind.getWindX(x, z);
		float zWind = 1f-2*World.wind.getWindZ(x, z);
		
		int numSplits = 20;
		numSplits = (int) Math.ceil(numSplits*height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		for (int i = 0; i < numSplits/2; ++i) {
			float colorGrad = 0.5f+(0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.1f*((float)i)/numSplits;
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
			force.x = World.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = World.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 50f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
		}
		
	}

	private void renderTreeTopAt(float height, float x, float z, int pos, float heightScale) {
		float[] c = Constants.Colors.TREE;
		float y = (float)Math.pow(World.terrain.height[pos], 1.5);
		y *= heightScale;
		float xWind = 1f-2*World.wind.getWindX(x, z);
		float zWind = 1f-2*World.wind.getWindZ(x, z);
		
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
				glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
				force.y = 20f; // Stiffness, force towards middle TODO: Make a force normal from ground
			}
			else {
				force.y = 50f; // Stiffness, force towards middle TODO: Make a force normal from ground
			}
			force.x = World.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = World.wind.getWindForceAtY(zWind, drawPos.y);
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			if (i > numSplits/2) {
				glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
			}

		}
	}

	private void renderGrassAt(float height, float x, float z, int pos, float heightScale) {
		float[] c = Constants.Colors.GRASS_STRAW;
		float y = (float)Math.pow(World.terrain.height[pos], 1.5);
		y *= heightScale;
		float xWind = 1f-2*World.wind.getWindX(x, z);
		float zWind = 1f-2*World.wind.getWindZ(x, z);
		
		int numSplits = grassQuality;
		numSplits = (int) Math.ceil(numSplits*height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		for (int i = 0; i < numSplits; ++i) {
			float colorGrad = 1f - (0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.4f*((float)i)/numSplits;
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
			force.x = World.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = World.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 4f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
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
