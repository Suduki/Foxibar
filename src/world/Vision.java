package world;

import agents.Animal;
import constants.Constants;

public class Vision {

	private int[] mFirstVision;	// The best vision value
	private int[] mFirstOwner;		// The closest animal
	private int[] mSecondVision;	// The superpositioned vision value of all nearby animals
	private int[] mSecondOwner;		// The 2nd closest animal

	private static final int VISION_WIDTH = 30;

	private static final int INVALID_OWNER = -1;

	public Vision() {
		mSecondVision  = new int[Constants.WORLD_SIZE];
		mFirstVision = new int[Constants.WORLD_SIZE];
		mFirstOwner   = new int[Constants.WORLD_SIZE];
		mSecondOwner  = new int[Constants.WORLD_SIZE];
	}

	public void update() {

		// First iteration. Reset.
		// For every world index empty the vision.
		// Set the vision to max if an animal is present.
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			mSecondVision[i] = 0;
			mSecondOwner[i] = INVALID_OWNER;
			if (Animal.containsAnimals[i] != Animal.INVALID_ID) {
				mFirstVision[i] = VISION_WIDTH;
				mFirstOwner[i] = Animal.containsAnimals[i];
			}
			else {
				mFirstVision[i] = 0;
				mFirstOwner[i] = INVALID_OWNER;
			}
		}

		// Second iteration. Spread the vision to east and west from where animals are positioned.
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (Animal.containsAnimals[i] != Animal.INVALID_ID) {
				int east = i;
				int west = i;
				// Go to east and west and spread the vision :)
				for (int j = VISION_WIDTH; j > 0 ; --j, east = World.east[east], west = World.west[west]) {
					int visionValue = j;
					int animalId = Animal.containsAnimals[i];
					if (mFirstOwner[east] != animalId) {
						if (mFirstVision[east] < visionValue) {
							// Take over first vision.
							mSecondVision[east] = mFirstVision[east];
							mSecondOwner[east] = mFirstOwner[east];
							mFirstVision[east] = visionValue;
							mFirstOwner[east] = animalId;
						}
						else if (mSecondVision[east] < visionValue) {
							// Take over second vision.
							mSecondVision[east] = visionValue;
							mSecondOwner[east] = animalId;
						}
					}
					
					if (mFirstOwner[west] != animalId) {
						if (mFirstVision[west] < visionValue) {
							// Take over first vision.
							mSecondVision[west] = mFirstVision[west];
							mSecondOwner[west] = mFirstOwner[west];
							mFirstVision[west] = visionValue;
							mFirstOwner[west] = animalId;
						}
						else if (mSecondVision[west] < visionValue) {
							// Take over second vision.
							mSecondVision[west] = visionValue;
							mSecondOwner[west] = animalId;
						}
					}
					
					
					// Northwest, southwest, northeast, northwest
					int northWest = World.north[west];
					int southWest = World.south[west];
					int northEast = World.north[east];
					int southEast = World.south[east];
					for (int k = j-1; k > 0; --k, 
							northWest = World.north[northWest],
							southWest = World.south[southWest],
							northEast = World.north[northEast],
							southEast = World.south[southEast]) {
						
						visionValue = k;
						setValue(northWest, visionValue, animalId);
						setValue(southWest, visionValue, animalId);
						
						if (j < VISION_WIDTH) {
							setValue(northEast, visionValue, animalId);
							setValue(southEast, visionValue, animalId);
						}
					}
				}
			}
		}
	}
	
	private void setValue(int pos, int visionValue, int animalId) { // TODO investigate return boolean, break on false.
		if (mFirstVision[pos] < visionValue) {
			// Take over first vision.
			mSecondVision[pos] = mFirstVision[pos];
			mSecondOwner[pos] = mFirstOwner[pos];
			mFirstVision[pos] = visionValue;
			mFirstOwner[pos] = animalId;
		}
		else if (mSecondVision[pos] < visionValue) {
			// Take over second vision.
			mSecondVision[pos] = visionValue;
			mSecondOwner[pos] = animalId;
		}
	}

	public void updateColor(float[] color, int i) {
		color[0] = 0;
		color[1] = 0;
		color[2] = 0;
		if (mFirstVision[i] != 0) {
			color[2] = ((float)mFirstVision[i])/VISION_WIDTH;
		}
		if (mSecondVision[i] != 0) {
			color[1] = ((float)mSecondVision[i])/VISION_WIDTH;
		}
		
//		if (mSecondOwner[i] != -1) {
//			color[0] = 1;
//		}
	}
}
