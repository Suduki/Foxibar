package agents;

import java.awt.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Species implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String FILE_NAME = "species.ser";

	public static ArrayList<Species> speciesList = new ArrayList<Species>();

	public int speciesId;
	public int numAlive;

	public float[] color, secondaryColor;

	public Brain bestBrain;
	public float bestScore;
	public boolean timeToSave;

	public float fightSkill;

	public Species(float[] color, float[] secondaryColor) {
		speciesList.add(this);

		this.color = color;
		this.secondaryColor = secondaryColor;
		speciesId = speciesList.size();
		bestBrain = new Brain(true);
		fightSkill = 1;
	}
	public float getUglySpeciesFactor() {
		if (numAlive > 100) {
			return 200f;
		}
		else if (numAlive > 1000) {
			return 2000f;
		}
		return 1f;
	}


	public void someoneWasBorn() {
		numAlive++;
	}

	public void someoneDied(Animal agent) {
		if (agent.score >= bestScore && agent.score > 5) {
			System.out.println("new best species of id " + speciesId);
			bestScore = agent.score;
			bestBrain.inherit(agent.brain);
		}
		numAlive--;
	}
	public static Species getSpeciesFromId(int id) {
		return speciesList.get(id);
	}

	public static void saveSpecies() {
		try {
			FileOutputStream fileOut =
					new FileOutputStream( FILE_NAME);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(speciesList);
			out.close();
			fileOut.close();
			System.out.printf("Serialized species is saved in " + fileOut);
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static boolean loadSpeciesFromEarlierRun() {
		boolean success = false;
		System.out.println("Trying to load species from earlier run");
		try {
			FileInputStream fileIn = new FileInputStream( FILE_NAME);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Object o = in.readObject();
			if (o instanceof ArrayList) {
				if (((ArrayList<?>) o).isEmpty()) {
					System.err.println("Invalid read1 of serialized SpeciesList");
				}
				else if (((ArrayList<?>) o).get(0) instanceof Species) {
					System.out.println("Successful load from earlier run");
					speciesList = (ArrayList<Species>) o; 
					success = true;
				}
				else {
					System.err.println("Trying to load something different than Species?");
				}
			}
			else {
				System.err.println("Invalid read2 of serialized SpeciesList");
			}
			in.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file " + FILE_NAME);
			return success;
		} catch (IOException i) {
			i.printStackTrace();
			return success;
		} catch (ClassNotFoundException e) {
			return success;
		} 
		
		return success;
	}
}
