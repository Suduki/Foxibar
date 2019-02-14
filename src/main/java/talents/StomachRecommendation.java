package talents;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StomachRecommendation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String bloodFile = "serializedSettings/bloodRecommendation.ser";
	public static final String grassFile = "serializedSettings/grassRecommendation.ser";
	public static final String fiberFile = "serializedSettings/fiberRecommendation.ser";

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
	
	public void save(String toFile) {
		FileOutputStream f;
		try {
			f = new FileOutputStream(toFile);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static StomachRecommendation load(String fromFile) {
		FileInputStream f;
		try {
			f = new FileInputStream(fromFile);
			ObjectInputStream o = new ObjectInputStream(f);
			return (StomachRecommendation) o.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}