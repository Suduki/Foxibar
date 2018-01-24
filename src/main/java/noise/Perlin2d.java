package main.java.noise;

import java.math.BigInteger;
import java.util.Random;

import main.java.interpolator.CosineInterpolator;
import main.java.interpolator.Interpolator;

/**
 * Perlin2d.java
 *
 * @author Hoten
 */
public class Perlin2d {

    private Interpolator interpolator;
    private int p1, p2, p3, seed, octaves;
    private double persistence;
    private int[] p1s, p2s, p3s;

    public Perlin2d(double persistence, int octaves, int seed, Interpolator interpolator) {
        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        this.interpolator = interpolator;
        setPrimes(seed);
    }

    public Perlin2d(double persistence, int octaves, int seed) {
        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        interpolator = new CosineInterpolator();
        setPrimes(seed);
    }

    public double[][] tile(double[][] noise) {
        int w = noise.length / 2;
        int h = noise[0].length / 2;
        double[][] tiled = new double[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int x = i + w;
                int y = j + h;
                double sum = noise[x][y] * (w - i) * (h - j);
                sum += noise[x - w][y] * i * (h - j);
                sum += noise[x - w][y - h] * i * j;
                sum += noise[x][y - h] * (w - i) * j;
                tiled[i][j] = sum / (w * h);
            }
        }
        return tiled;
    }

    public double[][] createTiledArray(int width, int height) {
        double[][] tiledNoise = tile(createRawArray(width * 2, height * 2));
        clamp(tiledNoise);
        return tiledNoise;
    }

    public double[][] createArray(int width, int height) {
        double[][] noise = createRawArray(width, height);
        clamp(noise);
        return noise;
    }

    //not clamped
    private double[][] createRawArray(int width, int height) {
        double[][] y = new double[width][height];
        final int regionWidth = 3;
        int smallSeed = Math.abs(seed / 1000);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double nx = 1.0 * i / width * regionWidth;
                double ny = 1.0 * j / height * regionWidth;
                y[i][j] = perlinNoise2(smallSeed + nx, smallSeed + ny);
            }
        }
        return y;
    }

    private void clamp(double[][] noise) {
        int w = noise.length;
        int h = noise[0].length;
        double max = -1, min = 1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                max = Math.max(max, noise[i][j]);
                min = Math.min(min, noise[i][j]);
            }
        }
        double range = max - min;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                noise[i][j] = (noise[i][j] - min) / range;
            }
        }
    }

    private void setPrimes(int seed) {
        p1s = new int[octaves];
        p2s = new int[octaves];
        p3s = new int[octaves];
        for (int i = 0; i < octaves; ++i) {
            Random ran = new Random(i + seed);
            p1s[i] = BigInteger.probablePrime(23, ran).intValue();
            p2s[i] = BigInteger.probablePrime(24, ran).intValue();
            p3s[i] = BigInteger.probablePrime(25, ran).intValue();
        }
    }

    private double perlinNoise2(double x, double y) {
        double total = 0;
        int f = 1;
        double a = 1;
        for (int i = 0; i < octaves; ++i) {
            p1 = p1s[i];
            p2 = p2s[i];
            p3 = p3s[i];
            total += interpolatedNoise2(x * f, y * f) * a;
            f *= 2;
            a *= persistence;
        }
        return total;
    }

    private double noise2(int x, int y) {
        int n = x + y * 101;
        n = (n << 13) ^ n;
        return (1.0 - ((n * (n * n * p1 + p2) + p3) & 0x7fffffff) / 1073741824.0);
    }

    private double smoothedNoise2(int x, int y) {
        double corners = (noise2(x - 1, y - 1) + noise2(x + 1, y - 1) + noise2(x - 1, y + 1) + noise2(x + 1, y + 1)) / 16;
        double sides = (noise2(x - 1, y) + noise2(x + 1, y) + noise2(x, y - 1) + noise2(x, y + 1)) / 8;
        double center = noise2(x, y) / 4;
        return corners + sides + center;
    }

    private double interpolatedNoise2(double x, double y) {
        int intX = (int) x;
        int intY = (int) y;
        double fractionalX = x - intX;
        double fractionalY = y - intY;

        double v1 = smoothedNoise2(intX, intY);
        double v2 = smoothedNoise2(intX + 1, intY);
        double v3 = smoothedNoise2(intX, intY + 1);
        double v4 = smoothedNoise2(intX + 1, intY + 1);

        double i1 = interpolate(v1, v2, fractionalX);
        double i2 = interpolate(v3, v4, fractionalX);

        return interpolate(i1, i2, fractionalY);
    }

    private double interpolate(double v1, double v2, double fractionalX) {
        return interpolator.interpolate(v1, v2, fractionalX);
    }
}