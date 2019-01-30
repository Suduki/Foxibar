package plant;

import agents.Animal;
import agents.AgentManager;
import world.World;

public class Tree extends Animal {

	public Tree(World world, AgentManager agentManager) {
		super(world, agentManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void actionUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCloselyRelatedTo(Animal a) {
		// TODO Auto-generated method stub
		return false;
	}

}
