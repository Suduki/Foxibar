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
			for (circleVertice = 0; circleVertice < circle.vertices.length-1; ++circleVertice) {
				setColorBasedOnHeight(c, currentY - y + 0.5f * height, height, 0f);
				glVertex3f(currentX + currentHalfWidth * circle.vertices[circleVertice+1].x, currentY, currentZ + currentHalfWidth * circle.vertices[circleVertice+1].z);
				glVertex3f(currentX + currentHalfWidth * circle.vertices[circleVertice].x, currentY, currentZ + currentHalfWidth * circle.vertices[circleVertice].z);
				setColorBasedOnHeight(c, nextY - y + 0.5f * height, height, 0f);
				glVertex3f(nextX + nextHalfWidth * circle.vertices[circleVertice].x, nextY, nextZ + nextHalfWidth * circle.vertices[circleVertice].z);
				glVertex3f(nextX + nextHalfWidth * circle.vertices[circleVertice+1].x, nextY, nextZ + nextHalfWidth * circle.vertices[circleVertice+1].z);
			}
			
			setColorBasedOnHeight(c, currentY - y + 0.5f * height, height, 0f);
			glVertex3f(currentX + currentHalfWidth * circle.vertices[0].x, currentY, currentZ + currentHalfWidth * circle.vertices[0].z);
			glVertex3f(currentX + currentHalfWidth * circle.vertices[circleVertice].x, currentY, currentZ + currentHalfWidth * circle.vertices[circleVertice].z);
			setColorBasedOnHeight(c, nextY - y + 0.5f * height, height, 0f);
			glVertex3f(nextX + nextHalfWidth * circle.vertices[circleVertice].x, nextY, nextZ + nextHalfWidth * circle.vertices[circleVertice].z);
			glVertex3f(nextX + nextHalfWidth * circle.vertices[0].x, nextY, nextZ + nextHalfWidth * circle.vertices[0].z);
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

			Vector3f[] vertices = circle.rotateTowards(drawPos, radius);
			Vector3f[] nextVertices = nextCircle.rotateTowards(nextDrawPos, nextRadius);
			
			int i;
			
			float tempY;
			
			for (i = 0; i < circle.vertices.length-1; ++i) {
				tempY = oldMiddle.y + vertices[i].y;
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(oldMiddle.x + vertices[i].x, tempY, oldMiddle.z + vertices[i].z);
				
				tempY = nextMiddle.y + nextVertices[i].y;
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(nextMiddle.x + nextVertices[i].x, tempY, nextMiddle.z + nextVertices[i].z);
				
				tempY = nextMiddle.y + nextVertices[i+1].y;
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(nextMiddle.x + nextVertices[i+1].x, nextMiddle.y + nextVertices[i+1].y, nextMiddle.z + nextVertices[i+1].z);
				
				
				tempY = oldMiddle.y + vertices[i+1].y;
				setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
				glVertex3f(oldMiddle.x + vertices[i+1].x, tempY, oldMiddle.z + vertices[i+1].z);
			}
			
			tempY = oldMiddle.y + vertices[i].y;
			setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
			glVertex3f(oldMiddle.x + vertices[i].x, tempY, oldMiddle.z + vertices[i].z);
			
			tempY = nextMiddle.y + nextVertices[i].y;
			setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
			glVertex3f(nextMiddle.x + nextVertices[i].x, tempY, nextMiddle.z + nextVertices[i].z);
			
			tempY = nextMiddle.y + nextVertices[0].y;
			setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
			glVertex3f(nextMiddle.x + nextVertices[0].x, nextMiddle.y + nextVertices[0].y, nextMiddle.z + nextVertices[0].z);
			
			
			tempY = oldMiddle.y + vertices[0].y;
			setColorBasedOnHeight(c, tempY - (y + yStart), height, 0.05f);
			glVertex3f(oldMiddle.x + vertices[0].x, tempY, oldMiddle.z + vertices[0].z);
			
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
