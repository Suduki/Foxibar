package display.hex;

import main.Main;

import org.joml.Vector3f;

import display.Circle;

public class TubeRenderer {
	private final Circle circle;
	private final Circle maxCircle;
	
	private final Vector3f currentPos = new Vector3f();
	private final Vector3f nextPos = new Vector3f();
	
	public TubeRenderer(int numVertices, float[] bottomColor, float[] topColor) {
		circle = new Circle(numVertices, 1f, null);
		maxCircle = new Circle(numVertices, 1f, null);
	}
	
	public void render(Vector3f startPos, float height, float stiffness) {
		int numSplits = (int) (height + 2);
		
		float windX = Main.mSimulation.mWorld.wind.getWindXForce(startPos);
		float windZ = Main.mSimulation.mWorld.wind.getWindZForce(startPos);
		
		float splitHeight = height / numSplits;
		
		
		
		for (int i = 0; i < numSplits; ++i) {
			
		}
	}
	
	protected float heightToRadius(float height) {
		return height;
	}
	
	private void getNextPos(Vector3f v, float splitHeight) {
		
	}
}
