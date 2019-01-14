package world;

import constants.Constants;
import static constants.Constants.Neighbours.*;

import org.joml.Vector2f;
import org.joml.Vector2i;

import simulation.Simulation;
import vision.Vision;
import agents.Agent;
import agents.Agent;

public class World {

	public Terrain terrain;
	public Grass grass;
	public CarbonElement blood;
	public Wind wind;
	public Vision vision;
	
	public World(Vision vision) {
		terrain = new Terrain();
		grass = new Grass(terrain);
		blood = new CarbonElement(1, Constants.Colors.BLOOD, 1, Constants.Blood.DECAY_FACTOR);
		wind = new Wind();
		this.vision = vision;
		
		regenerate();
	}


	final static int UPDATE_FREQUENCY = 8;
	public void update(int timeStep) {
		
		grass.grow(timeStep, UPDATE_FREQUENCY);
		blood.decay(timeStep, UPDATE_FREQUENCY);
		wind.stepWind();

	}

	public void regenerate() {
		terrain.regenerate();
		grass.regenerate(true);
		wind.regenerate();
	}

	private static float[] tempColor = new float[3];
	
	public void updateColor(float[][][] terrainColor) {
		boolean useVisionColors = false;
		for (int x = 0; x < terrainColor.length; ++x) {
			for (int y = 0; y < terrainColor.length; ++y) {
				if (useVisionColors) {
					visionColor(terrainColor, x, y);
				}
				else {
					updateColor(terrainColor, x, y);
				}
			}
		}
		
	}
	
	public void visionColor(float[][][] a, int x, int y) {
		float[] color = vision.getColorAt(x, y);
		a[x][y][0] = color[0];
		a[x][y][1] = color[1];
		a[x][y][2] = color[2];
	}
	
	public void updateColor(float[][][] a, int x, int y) {
		float grassness, dirtness;

		a[x][y][0] = 0f;
		a[x][y][1] = 0f;
		a[x][y][2] = 0f;
		
		if (grass.tree.isAlive[x][y]) {
			a[x][y][0] = Constants.Colors.TREE[0];
			a[x][y][1] = Constants.Colors.TREE[1];
			a[x][y][2] = Constants.Colors.TREE[2];
			return;
		}

		grassness = grass.height[x][y];
//		if (!grass.toBeUpdated[pos]) {
//			a[x][y][2] += 0.7f;
//		}
		a[x][y][0] += grassness*grass.color[0];
		a[x][y][1] += grassness*grass.color[1];
		a[x][y][2] += grassness*grass.color[2];

		terrain.getColor(x, y, tempColor);
		dirtness = 1 - grassness;
		a[x][y][0] += dirtness*tempColor[0];
		a[x][y][1] += dirtness*tempColor[1];
		a[x][y][2] += dirtness*tempColor[2];

		// Find the highest pile of fiber/blood and use that color.
		blood.getColor(x, y, tempColor);
		a[x][y][0] += tempColor[0];
		a[x][y][1] += tempColor[1];
		a[x][y][2] += tempColor[2];
	}

	public void reset(boolean b) {
		grass.regenerate(b);
		blood.reset(b);
	}

	public static float wrapX(float f) {//TODO: Move to util class
		return wrap(f, Simulation.WORLD_SIZE_X);
	}
	public static float wrapY(float f) {//TODO: Move to util class
		return wrap(f, Simulation.WORLD_SIZE_Y);
	}
	public static float wrap(float a, int limMax) { //TODO: Move to util class
		if (Float.isNaN(a)) {
			System.err.println("Trying to wrap a NaN pos!!!");
			a = 0;
		}
		return ((a % limMax) + limMax) % limMax;
	}

	public static void wrap(Vector2f pos) {//TODO: Move to util class
		pos.x = wrapX(pos.x);
		pos.y = wrapY(pos.y);
	}
	
	public static int west(int x) {//TODO: Move to util class
		return (int) wrapX(x+1);
	}
	public static int east(int x) {//TODO: Move to util class
		return (int) wrapX(x-1);
	}
	public static int north(int y) {//TODO: Move to util class
		return (int) wrapY(y-1);
	}
	public static int south(int y) {//TODO: Move to util class
		return (int) wrapY(y+1);
	}


}
