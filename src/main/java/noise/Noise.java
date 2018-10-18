package noise;

import java.util.Random;

public class Noise {
	public static int seed = 366;

	public static float[][] generate(int width, int height, float whiteness){
		seed++;
	    Perlin2d pn = new Perlin2d(whiteness, 10, seed);
	    float[][] y = pn.createTiledArray(constants.Constants.WORLD_SIZE_X, constants.Constants.WORLD_SIZE_Y);
	    return y;
	}
}
