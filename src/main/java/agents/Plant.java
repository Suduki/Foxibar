package agents;

public class Plant extends Agent {

	public Plant(float health) {
		super(health);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Called once per simulation round.
	 * Updates the agent. 
	 * @return 
	 */
	@Override
	public boolean stepAgent() {
		if (!isAlive) {
			System.out.println("Trying to step a dead agent.");
			return isAlive;
		}
		harvest();
		if (size < maxSize) {
			grow();
		}
		if (health < maxHealth) {
			heal();
		}
		age();
		return isAlive;
	}
	
	@Override
	protected void harvest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void grow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void heal() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void die() {
		// TODO Auto-generated method stub
		
	}
	
}
