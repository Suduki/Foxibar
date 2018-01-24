package main.java.interpolator;

/**
 * LinearInterpolator.java
 *
 * @author Hoten
 */
public class LinearInterpolator implements Interpolator {

    @Override
    public double interpolate(double a, double b, double fractional) {
        return a * (1 - fractional) + b * fractional;
    }
}