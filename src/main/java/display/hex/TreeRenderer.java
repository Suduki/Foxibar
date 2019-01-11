package display.hex;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

import constants.Constants;
import display.Circle;
import main.Main;

public class TreeRenderer {
	Circle circle;
	
	public TreeRenderer() {
		super();
		this.circle = new Circle(6, 1, null);
	}
	

	void renderTreeAt(float height, float xPix, float zPix, int x, int z, float heightScale) {
		float y = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
		y *= heightScale;

		float scale = 0.3f;
		float treeTrunkHeight = 2* scale * (0.5f + (1f-0.5f)*height);
		float treeTrunkWidth = scale / 2;
		if (height < 1f) {
			treeTrunkWidth = scale / 2;
		}

		renderTreeTrunk(treeTrunkHeight, xPix, zPix, y, treeTrunkWidth);
		

		float yStart = treeTrunkHeight/4;
		float health = Main.mSimulation.mWorld.grass.getHealth(x, z);
		if (health > 0.2f) {
			float treeTopHeight = height * (health*0.3f + 0.7f) * scale /2;
			float treeTopWidth = height * health * scale /8;
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
			for (circleVertice = 0; circleVertice < circle.xVertices.length-1; ++circleVertice) {
				glVertex3f(currentX + currentHalfWidth * circle.xVertices[circleVertice+1], currentY, currentZ + currentHalfWidth * circle.zVertices[circleVertice+1]);
				glVertex3f(currentX + currentHalfWidth * circle.xVertices[circleVertice], currentY, currentZ + currentHalfWidth * circle.zVertices[circleVertice]);
				glVertex3f(nextX + nextHalfWidth * circle.xVertices[circleVertice], nextY, nextZ + nextHalfWidth * circle.zVertices[circleVertice]);
				glVertex3f(nextX + nextHalfWidth * circle.xVertices[circleVertice+1], nextY, nextZ + nextHalfWidth * circle.zVertices[circleVertice+1]);
			}
			
			glVertex3f(currentX + currentHalfWidth * circle.xVertices[0], currentY, currentZ + currentHalfWidth * circle.zVertices[0]);
			glVertex3f(currentX + currentHalfWidth * circle.xVertices[circleVertice], currentY, currentZ + currentHalfWidth * circle.zVertices[circleVertice]);
			glVertex3f(nextX + nextHalfWidth * circle.xVertices[circleVertice], nextY, nextZ + nextHalfWidth * circle.zVertices[circleVertice]);
			glVertex3f(nextX + nextHalfWidth * circle.xVertices[0], nextY, nextZ + nextHalfWidth * circle.zVertices[0]);
		}
	}

	private void renderTreeTop(float height, float width, float xPix, float zPix, float y, float yStart) {
		float[] c = Constants.Colors.TREE_TOP;
		glColor3f(c[0],c[1],c[2]);
		
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		float numSplits = Float.max(height*10, 2f);
		float treeTopHeightFactor = 2;

		float currentY = y + yStart;


		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();

		float oldMiddleX = xPix;
		float oldMiddleZ = zPix;
		for (int h = 0; h < numSplits; ++h) {
			force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y + yStart);
			force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y + yStart);
			force.y = 2f; // Stiffness, force towards middle TODO: Make a force normal from ground
			float factor = height / force.length() / numSplits;
			force.mul(factor);
			drawPos.add(force);

			float colorGrad = 0.5f+(0.5f*((float)h+1f)/numSplits);
			float alphaGrad = 0.2f*((float)h)/numSplits;
			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
			
			float radius = width * ((float) (h * (numSplits - h)))/(numSplits);
			float nextY = currentY + height * treeTopHeightFactor / numSplits;
			float nextMiddleX = xPix+drawPos.x;
			float nextMiddleZ = zPix+drawPos.z;

			renderTreeTopBottom(currentY, oldMiddleX, oldMiddleZ, radius);
			renderTreeTopTop(oldMiddleX, oldMiddleZ, nextY, radius);
			renderTreeTopSide(oldMiddleX, oldMiddleZ, currentY, nextY, radius);

			currentY = nextY;
			oldMiddleX = nextMiddleX;
			oldMiddleZ = nextMiddleZ;
		}
	}

	private void renderTreeTopBottom(float currentY, float nextMiddleX, float nextMiddleZ, float radius) {
		int i;
		for (i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(nextMiddleX + circle.getScaledXAt(i+1, radius), currentY, nextMiddleZ + circle.getScaledZAt(i+1, radius));
			glVertex3f(nextMiddleX, currentY, nextMiddleZ);
			glVertex3f(nextMiddleX, currentY, nextMiddleZ);
			glVertex3f(nextMiddleX + circle.getScaledXAt(i, radius), currentY, nextMiddleZ + circle.getScaledZAt(i, radius));
		}
		glVertex3f(nextMiddleX + circle.getScaledXAt(0, radius), currentY, nextMiddleZ + circle.getScaledZAt(0, radius));
		glVertex3f(nextMiddleX, currentY, nextMiddleZ);
		glVertex3f(nextMiddleX, currentY, nextMiddleZ);
		glVertex3f(nextMiddleX + circle.getScaledXAt(i, radius), currentY, nextMiddleZ + circle.getScaledZAt(i, radius));
	}

	private void renderTreeTopTop(float oldMiddleX, float oldMiddleZ, float nextY, float radius) {
		int i;
		for (i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(oldMiddleX + circle.getScaledXAt(i, radius), nextY, oldMiddleZ + circle.getScaledZAt(i, radius));
			glVertex3f(oldMiddleX, nextY, oldMiddleZ);
			glVertex3f(oldMiddleX, nextY, oldMiddleZ);
			glVertex3f(oldMiddleX + circle.getScaledXAt(i+1, radius), nextY, oldMiddleZ + circle.getScaledZAt(i+1, radius));
		}
		glVertex3f(oldMiddleX + circle.getScaledXAt(i, radius), nextY, oldMiddleZ + circle.getScaledZAt(i, radius));
		glVertex3f(oldMiddleX, nextY, oldMiddleZ);
		glVertex3f(oldMiddleX, nextY, oldMiddleZ);
		glVertex3f(oldMiddleX + circle.getScaledXAt(0, radius), nextY, oldMiddleZ + circle.getScaledZAt(0, radius));
	}

	private void renderTreeTopSide(float oldMiddleX, float oldMiddleZ, float currentY, float nextY, float radius) {
		int i;
		for (i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(oldMiddleX + circle.getScaledXAt(i+1, radius), currentY, oldMiddleZ + circle.getScaledZAt(i+1, radius));
			glVertex3f(oldMiddleX + circle.getScaledXAt(i, radius), currentY, oldMiddleZ + circle.getScaledZAt(i, radius));
			glVertex3f(oldMiddleX + circle.getScaledXAt(i, radius), nextY, oldMiddleZ + circle.getScaledZAt(i, radius));
			glVertex3f(oldMiddleX + circle.getScaledXAt(i+1, radius), nextY, oldMiddleZ + circle.getScaledZAt(i+1, radius));
		}
		glVertex3f(oldMiddleX + circle.getScaledXAt(0, radius), currentY, oldMiddleZ + circle.getScaledZAt(0, radius));
		glVertex3f(oldMiddleX + circle.getScaledXAt(i, radius), currentY, oldMiddleZ + circle.getScaledZAt(i, radius));
		glVertex3f(oldMiddleX + circle.getScaledXAt(i, radius), nextY, oldMiddleZ + circle.getScaledZAt(i, radius));
		glVertex3f(oldMiddleX + circle.getScaledXAt(0, radius), nextY, oldMiddleZ + circle.getScaledZAt(0, radius));
	}

}
