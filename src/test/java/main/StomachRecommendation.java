package main;

import java.io.Serializable;

public class StomachRecommendation implements Serializable {
	private static final long serialVersionUID = 1L;

	public StomachRecommendation(float lowLimit, float highLimit) {
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
		setMean();
	}
	public StomachRecommendation() {
		this.lowLimit = -1F;
		this.highLimit = -1F;
		setMean();
	}

	public Float lowLimit;
	public Float highLimit;
	public Float mean;

	public void printStuff() {
		System.out.println(toString());
	}
	public void setMean() {
		mean = (lowLimit + highLimit)/2;
	}

	@Override
	public String toString() {
		return "Low: " + lowLimit + " High: " + highLimit + " Mean: " + mean;
	}
}