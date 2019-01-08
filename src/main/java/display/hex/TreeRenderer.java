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
	

	void renderTreeAt(float height, float xPix, float zPix, int x, int z, float heightScale, boolean renderTreeTop) {
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

		for (int i = 0; i < circle.xVertices.length; ++i) {
			circle.xVertices[i] = treeTrunkWidth * circle.xVertices[i];
			circle.zVertices[i] = treeTrunkWidth * circle.zVertices[i];
		}


		renderTreeTrunk(xPix, zPix, y, treeTrunkHeight);

		if (renderTreeTop) {
			renderTreeTop(height, xPix, zPix, y, treeTrunkHeight);			
		}

	}

	private void renderTreeTrunk(float xPix, float zPix, float y, float treeTrunkHeight) {

		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		float numSplits = treeTrunkHeight + 2;
		float heightFactor = 2;
		float widthScale = 1f;
		float widthFactor = 0.5f * (widthScale  + (1f - widthScale) * treeTrunkHeight);

		float currentY = y+treeTrunkHeight-treeTrunkHeight * heightFactor / numSplits;

		Vector3f drawPos = new Vector3f();
		Vector3f force = new Vector3f();
		
		float nextX = xPix;
		float nextY = y;
		float nextZ = zPix;
		
		
		for (int i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(xPix, y+treeTrunkHeight*2+0.1f, zPix);
			glVertex3f(xPix + circle.xVertices[i+1], y, zPix + circle.zVertices[i+1]);
			glVertex3f(xPix + circle.xVertices[i], y, zPix + circle.zVertices[i]);
			glVertex3f(xPix, y+treeTrunkHeight*2+0.1f, zPix);
		}
	}
//
//	float nextX = xPix;
//	float nextY = y;
//	float nextZ = zPix;
//
//	float currentX = nextX;
//	float currentY = nextY;
//	float currentZ = nextZ;
//
//	for (int i = 0; i < numSplits; ++i) {
//		float colorGrad = getColorGrad(numSplits, i);
//		float alphaGrad = getAlphaGrad(numSplits, i);
//		float nextColorGrad = getColorGrad(numSplits, i+1);
//		float nextAlphaGrad = getAlphaGrad(numSplits, i+1);
//		
//		float grassWidth = 0.03f;
//		
//		
//		float currentHalfWidth = (grassWidth * (numSplits - i)) / numSplits;
//		float nextHalfWidth = (grassWidth * (numSplits - (i+1))) / numSplits;
//
//		currentX = nextX;
//		currentY = nextY;
//		currentZ = nextZ;
//
//		force.x = Main.mSimulation.mWorld.wind.getWindForceAtY(xWind, drawPos.y);
//		force.z = Main.mSimulation.mWorld.wind.getWindForceAtY(zWind, drawPos.y);
//		force.y = 4f; // Stiffness, force towards middle TODO: Make a force normal from ground
//		float factor = height / force.length() / numSplits;
//		force.mul(factor);
//		drawPos.add(force);
//		nextX = xPix + drawPos.x;
//		nextY = y + drawPos.y;
//		nextZ = zPix + drawPos.z;
//
//		for (int circleVertice = 0; circleVertice < circle.xVertices.length-1; ++circleVertice) {
//			glColor4f(c[0]*colorGrad,c[1]*colorGrad,c[2]*colorGrad, 1f - alphaGrad);
//			glVertex3f(currentX + currentHalfWidth * circle.xVertices[circleVertice+1], currentY, currentZ + currentHalfWidth * circle.zVertices[circleVertice+1]);
//			glVertex3f(currentX + currentHalfWidth * circle.xVertices[circleVertice], currentY, currentZ + currentHalfWidth * circle.zVertices[circleVertice]);
//			
//			glColor4f(c[0]*nextColorGrad,c[1]*nextColorGrad,c[2]*nextColorGrad, 1f - nextAlphaGrad);
//			glVertex3f(nextX + nextHalfWidth * circle.xVertices[circleVertice], nextY, nextZ + nextHalfWidth * circle.zVertices[circleVertice]);
//			glVertex3f(nextX + nextHalfWidth * circle.xVertices[circleVertice+1], nextY, nextZ + nextHalfWidth * circle.zVertices[circleVertice+1]);
//		}
//	}


	private void renderTreeTop(float height, float xPix, float zPix, float y,
			float treeTrunkHeight) {
		float[] c;
		c = Constants.Colors.TREE_TOP;
		glColor3f(c[0],c[1],c[2]);
		float xWind = 1f-2*Main.mSimulation.mWorld.wind.getWindX(xPix, zPix);
		float zWind = 1f-2*Main.mSimulation.mWorld.wind.getWindZ(xPix, zPix);

		float numSplits = height + 2;
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

			for (int i = 0; i < circle.xVertices.length; ++i) {
				circle.xVertices[i] = drawPos.x + treeTopWidthFactor * circle.xVertices[i] * (h * (numSplits - h))/numSplits;
				circle.zVertices[i] = drawPos.z + treeTopWidthFactor * circle.zVertices[i] * (h * (numSplits - h))/numSplits;
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
		for (int i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(xPix+circle.xVertices[i+1], currentY, zPix+circle.zVertices[i+1]);
			glVertex3f(nextMiddleX, currentY, nextMiddleZ);
			glVertex3f(nextMiddleX, currentY, nextMiddleZ);
			glVertex3f(xPix+circle.xVertices[i], currentY, zPix+circle.zVertices[i]);
		}
	}

	private void renderTreeTopTop(float xPix, float zPix, float oldMiddleX, float oldMiddleZ, float nextY) {
		for (int i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(xPix+circle.xVertices[i], nextY, zPix+circle.zVertices[i]);
			glVertex3f(oldMiddleX, nextY, oldMiddleZ);
			glVertex3f(oldMiddleX, nextY, oldMiddleZ);
			glVertex3f(xPix+circle.xVertices[i+1], nextY, zPix+circle.zVertices[i+1]);
		}
	}

	private void renderTreeTopSide(float xPix, float zPix, float currentY, float nextY) {
		for (int i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(xPix+circle.xVertices[i+1], currentY, zPix+circle.zVertices[i+1]);
			glVertex3f(xPix+circle.xVertices[i], currentY, zPix+circle.zVertices[i]);
			glVertex3f(xPix+circle.xVertices[i], nextY, zPix+circle.zVertices[i]);
			glVertex3f(xPix+circle.xVertices[i+1], nextY, zPix+circle.zVertices[i+1]);
		}
	}

}
