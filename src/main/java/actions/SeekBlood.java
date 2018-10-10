package actions;

import org.joml.Vector2f;

import agents.Agent;

public class SeekBlood extends Action {
	public Vector2f dir;
	public float bloodness;

	public SeekBlood() {
		dir = new Vector2f();
	}

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = false;
		bloodness = a.seekBlood(dir);
		if (bloodness > 0.05f) {
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
		a.harvestBlood();
		a.harvestFat();
	}
}
