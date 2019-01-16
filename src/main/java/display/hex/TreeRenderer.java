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
	Circle circle;
	Circle nextCircle;
	
	public TreeRenderer() {
		super();
		this.circle = new Circle(10, 1, null);
		this.nextCircle = new Circle(10, 1, null);
	}
	

	void renderTreeAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;

		float scale = 0.2f;
		float treeTrunkHeight = 2* scale * (0.5f + (1f-0.5f)*height);
		float treeTrunkWidth = scale;
		if (height < 1f) {
			treeTrunkWidth = scale / 2;
		}

		renderTreeTrunk(treeTrunkHeight, xPix, zPix, y, treeTrunkWidth);
		

		float yStart = treeTrunkHeight/6;
		float health = Main.mSimulation.mWorld.grass.getHealth(x, z);
		if (health > 0.05f) {
			float treeTopHeight = height * (health*0.3f + 0.7f) * scale /2 + 0.2f;
			float treeTopWidth = height * health * scale*0.1f + 0.2f;
			renderTreeTop(treeTopHeight, treeTopWidth, xPix, zPix, y, yStart);			
		}

	}
	
	Vector3f drawPos = new Vector3f();
	Vector3f force = new Vector3f();

	private void renderTreeTrunk(float height, float xPix, float zPix, float y, float width) {
		float[] c = Constants.Colors.TREE;
		glColor3f(c[0],c[1],c[2]);
		
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		int numSplits = (int)height+2;
		
		drawPos.set(0);
		force.set(0);

		float nextX = xPix;
		float nextY = y;
		float nextZ = zPix;

		float currentX = nextX;
		float currentY = nextY;
		float currentZ = nextZ;

		for (int i = 0; i < numSplits; ++i) {
			float currentHalfWidth = (width * (numSplits - i)) / numSplits;
			float nextHalfWidth = (width * (numSplits - (i+1))) / numSplits;
			
			currentX = nextX;
			currentY = nextY;
			currentZ = nextZ;

			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
			force.y = 10f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);
			nextX = xPix + drawPos.x;
			nextY = y + drawPos.y;
			nextZ = zPix + drawPos.z;

			int circleVertice;
			for (circleVertice = 0; circleVertice < circle.vertices.length; ++circleVertice) {
				setColorBasedOnHeight(c, currentY - y + 0.5f * height, height, 0f);
				glVertex3f(currentX + circle.getScaledXAt(circleVertice+1, currentHalfWidth), currentY, currentZ + circle.getScaledZAt(circleVertice+1, currentHalfWidth));
				glVertex3f(currentX + circle.getScaledXAt(circleVertice, currentHalfWidth), currentY, currentZ + circle.getScaledZAt(circleVertice, currentHalfWidth));
				
				setColorBasedOnHeight(c, nextY - y + 0.5f * height, height, 0f);
				glVertex3f(nextX + circle.getScaledXAt(circleVertice, nextHalfWidth), nextY, nextZ + circle.getScaledZAt(circleVertice, nextHalfWidth));
				glVertex3f(nextX + circle.getScaledXAt(circleVertice+1, nextHalfWidth), nextY, nextZ + circle.getScaledZAt(circleVertice+1, nextHalfWidth));
			}
		}
	}

	private void renderTreeTop(float height, float width, float xPix, float zPix, float y, float yStart) {
		float[] c = Constants.Colors.TREE_TOP;
		glColor3f(c[0],c[1],c[2]);
		
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		float numSplits = Float.max(height*10, 4f);
		float treeTopHeightFactor = 2;

		float currentY = y + yStart;

		Vector3f drawPos = new Vector3f();
		Vector3f nextDrawPos = new Vector3f();
		Vector3f force = new Vector3f();
		Vector3f nextForce = new Vector3f(); 

		Vector3f oldMiddle = new Vector3f(xPix, 0, zPix);
		Vector3f nextMiddle = new Vector3f();
		
		float nextY = 0;
		float radius = 0;
		
		for (int h = 0; h < numSplits; ++h) {
			nextForce.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, nextY);
			nextForce.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, nextY);
			nextForce.y = 10f; // Stiffness, force towards middle TODO: Make a force normal from ground
			
			float factor = height / nextForce.length() / numSplits;
			nextForce.mul(factor);
			nextDrawPos.add(force);

			float nextRadius = Float.max(width * ((float) ((h-1) * (numSplits - (h+1))))/(numSplits), 0);
			nextY = h + 1 < numSplits ? currentY + height * treeTopHeightFactor / numSplits : currentY + (numSplits % 1)/10;
			
			nextMiddle.set(xPix+nextDrawPos.x, nextY, zPix+nextDrawPos.z);

			circle.rotateTowards(drawPos);
			nextCircle.rotateTowards(nextDrawPos);
			
			int i;
			
			float tempY;
			
			for (i = 0; i < circle.vertices.length; ++i) {
				tempY = oldMiddle.y + circle.getScaledYAt(i, radius);
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(oldMiddle.x + circle.getScaledXAt(i, radius), tempY, oldMiddle.z + circle.getScaledZAt(i, radius));
				
				tempY = nextMiddle.y + nextCircle.getScaledYAt(i, nextRadius);
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(nextMiddle.x + nextCircle.getScaledXAt(i, nextRadius), tempY, nextMiddle.z + nextCircle.getScaledZAt(i, nextRadius));
				
				tempY = nextMiddle.y + nextCircle.getScaledYAt(i+1, nextRadius);
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(nextMiddle.x + nextCircle.getScaledXAt(i+1, nextRadius), tempY, nextMiddle.z + nextCircle.getScaledZAt(i+1, nextRadius));
				
				
				tempY = oldMiddle.y + circle.getScaledYAt(i+1, radius);
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(oldMiddle.x + circle.getScaledXAt(i+1, radius), tempY, oldMiddle.z + circle.getScaledZAt(i+1, radius));
			}
			
			circle.resetCircle();
			nextCircle.resetCircle();

			currentY = nextY;
			radius = nextRadius;
			oldMiddle.set(nextMiddle);
			force.set(nextForce);
			drawPos.set(nextDrawPos);
		}
	}


	private float getColorGrad(float y, float yMax) {
		
		return Float.min((y)/yMax, 1);
	}
	
	private void setColorBasedOnHeight(float[] c, float y, float yMax, float alpha) {
		glColor4f(c[0] * getColorGrad(y, yMax), c[1] * getColorGrad(y, yMax), c[2] * getColorGrad(y, yMax), 1f - alpha);
	}
}
