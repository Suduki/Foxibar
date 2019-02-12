package actions;

import agents.Animal;

public class HuntStranger extends Hunt {
	
	public HuntStranger() {
		super();
	}
	
	@Override
	protected Animal getAnimalToHunt(Animal a) {
		return a.stranger;
	}
}
