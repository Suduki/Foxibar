package noise;

import simulation.Simulation;

public class Noise {
	public static int seed = 366;

	public static float[][] generate(int width, int height, float whiteness){
		seed++;
	    Perlin2d pn = new Perlin2d(whiteness, 10, seed);
	    float[][] y = pn.createTiledArray(Simulation.WORLD_SIZE_X, Simulation.WORLD_SIZE_Y);
	    return y;
	}
}
