package noise;

import java.util.Random;

public class Noise {
	public static int seed = 366;
	private static Random random;

	private static float[][] generateWhiteNoise(int width, int height) {
	    float[][] noise = new float[width][height];

	    for (int i = 0; i < width; i++) {
	        for (int j = 0; j < height; j++) {
	            noise[i][j] = (float) random.nextDouble();
	        }
	    }

	    return noise;
	}

	private static float[][] generateSmoothNoise(float[][] baseNoise, int octave) {
	    int width = baseNoise.length;
	    int height = baseNoise[0].length;

	    float[][] smoothNoise = new float[width][height];

	    int samplePeriod = 1 << octave; // calculates 2 ^ k
	    float sampleFrequency = 1.0f / samplePeriod;

	    for (int i = 0; i < width; i++) {
	        // calculate the horizontal sampling indices
	        int sample_i0 = (i / samplePeriod) * samplePeriod;
	        int sample_i1 = (sample_i0 + samplePeriod) % width; // wrap around
	        float horizontal_blend = (i - sample_i0) * sampleFrequency;

	        for (int j = 0; j < height; j++) {
	            // calculate the vertical sampling indices
	            int sample_j0 = (j / samplePeriod) * samplePeriod;
	            int sample_j1 = (sample_j0 + samplePeriod) % height; // wrap
	                                                                    // around
	            float vertical_blend = (j - sample_j0) * sampleFrequency;

	            // blend the top two corners
	            float top = interpolate(baseNoise[sample_i0][sample_j0],
	                    baseNoise[sample_i1][sample_j0], horizontal_blend);

	            // blend the bottom two corners
	            float bottom = interpolate(baseNoise[sample_i0][sample_j1],
	                    baseNoise[sample_i1][sample_j1], horizontal_blend);

	            // final blend
	            smoothNoise[i][j] = interpolate(top, bottom, vertical_blend);
	        }
	    }

	    return smoothNoise;
	}

	private static float interpolate(float x0, float x1, float alpha) {
	    return x0 * (1 - alpha) + alpha * x1;
	}

	private static float[][][] generatePerlinNoise(float[][] baseNoise,
	        int octaveCount) {
	    int width = baseNoise.length;
	    int height = baseNoise[0].length;
	    int layersToSkip = 0;//Math.min(4, octaveCount/2);

	    
	    float[] normalizer = new float[2];
	    normalizer[0] = 0;
	    normalizer[1] = 0;
	    
	    float[][] persistance = new float[2][octaveCount];
	    for (int i = 0; i < octaveCount; ++i) {
	    	if (i < layersToSkip) {
	    		persistance[0][i] = 0;
	    	}
	    	else {
	    		persistance[0][i] = 3+i;
	    	}
    		persistance[1][i] = octaveCount-i+10;
	    	normalizer[0] += persistance[0][i];
	    	normalizer[1] += persistance[1][i];
	    }
	    
	    float[][][] smoothNoise = new float[octaveCount][][];

	    // generate smooth noise
	    for (int i = 0; i < octaveCount; i++) {
	        smoothNoise[i] = generateSmoothNoise(baseNoise, i);
	        persistance[0][i] /= normalizer[0];
	        persistance[1][i] /= normalizer[1];
	    }

	    float[][][] perlinNoise = new float[2][width][height];

	    // blend noise together
	    for (int octave = octaveCount - 1; octave >= 0; octave--) {
	        for (int i = 0; i < width; i++) {
	            for (int j = 0; j < height; j++) {
	                perlinNoise[0][i][j] += smoothNoise[octave][i][j] * persistance[0][octave];
	                perlinNoise[1][i][j] += smoothNoise[octave][i][j] * persistance[1][octave];
	            }
	        }
	    }

	    return perlinNoise;
	}
	
	public static float[][][] generate(int width, int height){
		seed++;
		random = new Random(seed);
		float[][][] noise = generatePerlinNoise(generateWhiteNoise(width, height), Math.min(constants.Constants.WORLD_MULTIPLIER , 7));
		return noise;
	//...
	}
}
