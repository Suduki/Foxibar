package world;

import org.joml.Vector3f;

import noise.Noise;
import constants.Constants;
import display.RenderState;

public class Terrain {
	public static final float WATER_LIMIT = 0.4f;
	public static final float STONE_LIMIT = 0.8f;
	public float[] height;
	public float[] growth;
	
	public float[] windX; //TODO: Only used by rendering. Move!
	public float[] windZ; //TODO: Only used by rendering. Move!
	
	public boolean grass[];
	public boolean water[];
	public boolean stone[];
	
	public Terrain() {
		height = new float[Constants.WORLD_SIZE];
		growth = new float[Constants.WORLD_SIZE];
		
		windX = new float[Constants.WORLD_SIZE];
		windZ = new float[Constants.WORLD_SIZE];
		
		grass = new boolean[Constants.WORLD_SIZE];
		water = new boolean[Constants.WORLD_SIZE];
		stone = new boolean[Constants.WORLD_SIZE];
	}
	
	public  float[] getColor(int i, float[] color) {
		if (water[i]) {
			color[0] = Constants.Colors.BLUE[0]*(1f-height[i]);
			color[1] = Constants.Colors.BLUE[1]*(1f-height[i]);
			color[2] = Constants.Colors.BLUE[2]*(1f-height[i]);
		}
		else if (stone[i]) {
			color[0] = Constants.Colors.GREY[0]*height[i];
			color[1] = Constants.Colors.GREY[1]*height[i];
			color[2] = Constants.Colors.GREY[2]*height[i];
		}
		else if (RenderState.RENDER_DIRT){
			color[0] = Constants.Colors.DIRT[0]*growth[i] + Constants.Colors.SAND[0]*(1-growth[i]);
			color[1] = Constants.Colors.DIRT[1]*growth[i] + Constants.Colors.SAND[1]*(1-growth[i]);
			color[2] = Constants.Colors.DIRT[2]*growth[i] + Constants.Colors.SAND[2]*(1-growth[i]);
		}
		else {
			color[0] = 0;
			color[1] = 0;
			color[2] = 0;
		}
		return color;
	}

	public void regenerate() {
		double[][] noise = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
		analyzeNoise(noise);
		int i = 0;
		for(int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Constants.WORLD_SIZE_Y; ++y, ++i) {
				height[i] = (float) noise[x][y];
				water[i] = false;
				stone[i] = false;

				if (height[i] < WATER_LIMIT) {
					water[i] = true;
				}
				if (height[i] > STONE_LIMIT) {
					stone[i] = true;
				}
			}
		}
		
		noise = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
		analyzeNoise(noise);
		i = 0;
		for(int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Constants.WORLD_SIZE_Y; ++y, ++i) {
				growth[i] = (float) noise[x][y];
			}
		}
		
		noise = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
		analyzeNoise(noise);
		i = 0;
		for(int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Constants.WORLD_SIZE_Y; ++y, ++i) {
				windX[i] = (float) noise[x][y];
			}
		}
		
		noise = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
		analyzeNoise(noise);
		i = 0;
		for(int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Constants.WORLD_SIZE_Y; ++y, ++i) {
				windZ[i] = (float) noise[x][y];
			}
		}
	}
	
	
	private void analyzeNoise(double[][] noise) {
		double lowest, highest, avg = 0;
		lowest = noise[0][0];
		highest = noise[0][0];
		for (int i = 0; i < noise.length; ++i) {
			for (int j = 0; j < noise[i].length; ++j) {
				if (lowest > noise[i][j]) {
					lowest = noise[i][j];
				}
				if (highest < noise[i][j]) {
					highest = noise[i][j];
				}
				avg += noise[i][j];
			}
		}
		avg /= noise.length*noise[0].length;
		
		System.out.println("low = " + lowest + ", highest = " + highest + ", avg = " + avg);
	}
	
	private Vector3f windXOffset = new Vector3f(0,0,0);
	private Vector3f windZOffset = new Vector3f(0,0,0);
	private Vector3f windXSpeed = new Vector3f(0,0,0);
	private Vector3f windZSpeed = new Vector3f(0,0,0);
	public void stepWind() {
		float damping = 0.01f;
		float windAcceleration = 0.001f;
		windXSpeed.x += rand()*windAcceleration - windXSpeed.x*damping;
		windXSpeed.y += rand()*windAcceleration - windXSpeed.y*damping;
		
		windZSpeed.x += rand()*windAcceleration - windZSpeed.x*damping;
		windZSpeed.y += rand()*windAcceleration - windZSpeed.y*damping;
		
		windXOffset.add(windXSpeed);
		windZOffset.add(windZSpeed);
	}
	private float rand() {
		return 2 * Constants.RANDOM.nextFloat() - 1f;
	}

	public float getWindX(int pos) {
		return windX[wrap(pos + windXOffset.y * Constants.WORLD_SIZE_X + windXOffset.x,Constants.WORLD_SIZE)];
	}
	public float getWindZ(int pos) {
		return windZ[wrap(pos + windZOffset.y * Constants.WORLD_SIZE_X + windZOffset.x,Constants.WORLD_SIZE)];
	}
	
	public float getWindForceAtY(float windAtPos, float y) {
		return windAtPos*windAtPos * (y+0.4f);
	}
	
	private int wrap(float val, int max) {
		
		return  ((((int)val % max) + max) % max);
	}
	
}
