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
		float[] persistance = {0.5f, 0.5f, 0.5f, 0.5f, 0f, 0f, 0f}; // The first numbers corresponds to large areas of artifacts, the later numbers affect smaller artifacts.
	    Perlin2d pn = new Perlin2d(persistance, seed);
	    double[][] y = pn.createTiledArray(constants.Constants.WORLD_SIZE_X, constants.Constants.WORLD_SIZE_Y);
	    return y;
	}
}
