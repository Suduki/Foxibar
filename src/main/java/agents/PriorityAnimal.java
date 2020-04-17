package agents;

import actions.Action;
import actions.ActionManager;
import actions.ActionManager.Actions;
import world.World;

public class PriorityAnimal extends Animal {

	private Actions[] priorityQueue;

	public PriorityAnimal(World world, ActionManager actionManager, Actions[] priorityQueue) {
		super(world, actionManager);
		
		this.priorityQueue = priorityQueue;
	}

	@Override
	protected void actionUpdate() {
		for (Actions actionE : priorityQueue) {
			Action action = actionManager.getAction(actionE);
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
