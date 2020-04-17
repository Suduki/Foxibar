package actions;

import java.util.ArrayList;

import world.World;
import agents.Animal;

public abstract class Action {
	public int numCommits;
	public int numPossible;

	public int id;
	public static int nextId = 0;

	public boolean isPossible;

	public abstract boolean determineIfPossible(Animal a);
	public abstract void commit(Animal a);

	public Action() {
		id = nextId++;
	}
}
