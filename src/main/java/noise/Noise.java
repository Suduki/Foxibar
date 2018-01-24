package noise;

import java.util.Random;

public class Noise {
	public static int seed = 366;
	private static Random random;

	public static double[][] generate(int width, int height){
		seed++;
		random = new Random(seed);
//		float[][][] noise = generatePerlinNoise(generateWhiteNoise(width, height), Math.min(constants.Constants.WORLD_MULTIPLIER-2 , 7));
//		return noise;
	//...
		float whiteness = 0.5f; // Increase this for more whiteness.
	    Perlin2d pn = new Perlin2d(whiteness, 10, seed);
	    double[][] y = pn.createTiledArray(constants.Constants.WORLD_SIZE_X, constants.Constants.WORLD_SIZE_Y);
	    return y;
	}
}
