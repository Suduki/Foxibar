package world;

import simulation.Simulation;
import noise.Noise;
import constants.Constants;
import display.RenderState;

public class Terrain {
	public static final float WATER_LIMIT = 0.2f;
	public static final float STONE_LIMIT = 0.8f;
	public float[][] height;
	public float[][] growth;
	
	public boolean water[][];
	public boolean stone[][];
	
	public Terrain() {
		height = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		growth = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		
		water = new boolean[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		stone = new boolean[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
	}
	
	public  float[] getColor(int x, int y, float[] color) {
		if (water[x][y]) {
			color[0] = Constants.Colors.BLUE[0]*(1f-height[x][y]);
			color[1] = Constants.Colors.BLUE[1]*(1f-height[x][y]);
			color[2] = Constants.Colors.BLUE[2]*(1f-height[x][y]);
		}
		else if (stone[x][y]) {
			color[0] = Constants.Colors.GREY[0]*height[x][y];
			color[1] = Constants.Colors.GREY[1]*height[x][y];
			color[2] = Constants.Colors.GREY[2]*height[x][y];
		}
		else if (RenderState.RENDER_DIRT){
			color[0] = Constants.Colors.DIRT[0]*growth[x][y] + Constants.Colors.SAND[0]*(1-growth[x][y]);
			color[1] = Constants.Colors.DIRT[1]*growth[x][y] + Constants.Colors.SAND[1]*(1-growth[x][y]);
			color[2] = Constants.Colors.DIRT[2]*growth[x][y] + Constants.Colors.SAND[2]*(1-growth[x][y]);
		}
		else {
			color[0] = 0;
			color[1] = 0;
			color[2] = 0;
		}
		return color;
	}

	public void regenerate() {
		float[][] noise = Noise.generate(Simulation.WORLD_SIZE_X, Simulation.WORLD_SIZE_Y, 0.6f);
		analyzeNoise(noise);
		for(int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				height[x][y] = (float) noise[x][y];
				water[x][y] = false;
				stone[x][y] = false;

				if (growth[x][y] > STONE_LIMIT) {
					stone[x][y] = true;
				}
				if (height[x][y] < WATER_LIMIT) {
					water[x][y] = true;
				}
			}
		}
		
		noise = Noise.generate(Simulation.WORLD_SIZE_X, Simulation.WORLD_SIZE_Y, 0.5f);
		analyzeNoise(noise);
		for(int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				growth[x][y] = (float) noise[x][y];
			}
		}
	}
	
	
	private void analyzeNoise(float[][] noise) {
		float lowest, highest, avg = 0;
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
	

	
}
