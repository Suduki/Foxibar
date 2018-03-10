package display;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

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
				if (height > 0.3f) {
					float xScale = (float)(Math.sqrt(3)*0.5);
					float zScale = 1.5f;

					int hexX = x/2;
					int hexZ = z/2; 
					
					float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;
					
					float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
					float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);
					
					renderGrassAt(height*2, xpos, zpos, i, heightScale);
				}
				++i;
			}
		}
		glEnd();
		glLineWidth(1);
	}
	
	
	private void renderGrassAt(float height, float x, float z, int pos, float heightScale) {
		float[] c = Constants.Colors.GRASS_STRAW;
		float y = (float)Math.pow(World.terrain.height[pos], 1.5);
		y *= heightScale;
		float xWind = 1f-2*World.wind.getWindX(x, z);
		float zWind = 1f-2*World.wind.getWindZ(x, z);
		
		int numSplits = grassQuality;
		numSplits = (int) Math.ceil(numSplits/height);
		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		for (int i = 0; i < numSplits; ++i) {
			float colorGrad = 1f - (0.5f*((float)i+1f)/numSplits);
			float alphaGrad = 0.4f*((float)i)/numSplits;
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			glVertex3f(x + drawPos.x,y + drawPos.y,z + drawPos.z);
			force.x = World.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = World.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 1f; // Stiffness, force towards middle TODO: Make a force normal from ground
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
