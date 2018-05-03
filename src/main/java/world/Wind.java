package world;

import noise.Noise;

import org.joml.Vector3f;

import constants.Constants;

public class Wind {

	public Wind() {
		windX = new float[Constants.WORLD_SIZE_X][Constants.WORLD_SIZE_Y];
		windZ = new float[Constants.WORLD_SIZE_X][Constants.WORLD_SIZE_Y];
	}

	public float[][] windX;
	public float[][] windZ;
	
	private Vector3f windXOffset = new Vector3f(0,0,0);
	private Vector3f windZOffset = new Vector3f(0,0,0);
	private Vector3f windXSpeed = new Vector3f(0,0,0);
	private Vector3f windZSpeed = new Vector3f(0,0,0);
	public void stepWind() {
		float damping = 0.99f;
		float speed = 0.5f;
		windXSpeed.x += rand()*speed;
		windXSpeed.y += rand()*speed;
		
		windZSpeed.x += rand()*speed;
		windZSpeed.y += rand()*speed;
		
		windXSpeed.mul(damping);
		windZSpeed.mul(damping);
		
		windXOffset.add(windXSpeed);
		windZOffset.add(windZSpeed);
	}
	private float rand() {
		return 1f - 2 * Constants.RANDOM.nextFloat();
	}

	public float getWindX(float posX, float posY) {
		return windX[wrap(posX + windXOffset.x, Constants.WORLD_SIZE_X)][wrap(posY + windXOffset.y, Constants.WORLD_SIZE_Y)];
	}
	public float getWindZ(float posX, float posY) {
		return windZ[wrap(posX + windZOffset.x, Constants.WORLD_SIZE_X)][wrap(posY + windZOffset.y, Constants.WORLD_SIZE_Y)];
	}
	
	public float getWindForceAtY(float windAtPos, float y) {
		return windAtPos * (y+0.4f);
	}
	
	private int wrap(float val, int max) {
		
		return  ((((int)val % max) + max) % max);
	}
	public void regenerate() {
		windX = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, 0.5f);
		windZ = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, 0.5f);		
	}
}
