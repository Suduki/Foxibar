package agents;

import actions.Action;
import world.World;

public class PriorityAnimal extends Animal {

	private Action[] priorityQueue;

	public PriorityAnimal(World world, Action[] priorityQueue) {
		super(world);
		
		this.priorityQueue = priorityQueue;
	}

	@Override
	protected void actionUpdate() {
		for (Action action : priorityQueue) {
			if (action.isPossible) {
				action.commit(this);
				return;
			}
		}
		System.err.println("Should always be able to commit to an action");
		return;
	}

	@Override
	public boolean isCloselyRelatedTo(Animal a) {
		return isSameClassAs(a);
	}
}
