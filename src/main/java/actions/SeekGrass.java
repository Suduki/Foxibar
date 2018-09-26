package actions;

import org.joml.Vector2f;

import agents.Agent;

public class SeekGrass extends Action {
	public Vector2f dir;
	public float grassness;

	public SeekGrass() {
		dir = new Vector2f();
	}

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = false;
		grassness = a.seekGrass(dir);
		if (grassness > 0.1f) {
			isPossible = true;
		}
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCalls++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		a.vel.set(dir);
		a.move();
		a.harvestGrass();
		a.harvestFat();
	}
}
