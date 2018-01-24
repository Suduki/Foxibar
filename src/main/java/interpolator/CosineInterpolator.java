package interpolator;

/**
 * CosineInterpolator.java
 *
 * @author Hoten
 */
public class CosineInterpolator implements Interpolator {

    @Override
    public double interpolate(double a, double b, double fractional) {
        double ft = fractional * 3.1415927;
        double f = (1 - Math.cos(ft)) * .5;
        return a * (1 - f) + b * f;
    }
}