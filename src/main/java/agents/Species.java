package agents;

public class Species {
	
	public static final int BIRTH_HUNGER_COST = 5;
	public static final int AGE_DEATH = 1000;
	
	public int speciesId;
	public static int numSpecies = 0;
	public int numAlive;
	
	public float[] color, secondaryColor;
	
	public Brain bestBrain;
	public float bestScore;
	public boolean timeToSave;
	
	public Species(float[] color, float[] secondaryColor) {
		this.color = color;
		this.secondaryColor = secondaryColor;
		speciesId = numSpecies++;
		bestBrain = new Brain(true);
	}
	public float getUglySpeciesFactor() {
		if (numAlive > 500) {
			return 1f;
		}
		else if (numAlive > 1000) {
			return 2f;
		}
		return 0.5f;
	}
	
	
	public void someoneWasBorn(Species mom) {
		numAlive++;
	}
	
	public void someoneDied(Animal a) {
		if (a.score > bestScore) {
			bestScore = a.score;
			bestBrain.inherit(a.brain, a.brain);
			timeToSave = true;
		}
		numAlive--;
	}
	
}
