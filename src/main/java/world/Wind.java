package world;

import noise.Noise;

import org.joml.Vector3f;

import simulation.Simulation;
import constants.Constants;

public class Wind {

	public Wind() {
		windX = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		windZ = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
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

	public float getWindXForce(Vector3f atPosition) {
		return windX[(int) World.wrapX(atPosition.x + windXOffset.x)][(int) World.wrapY(atPosition.z + windXOffset.y)];
	}
	
	public float getWindZForce(Vector3f atPosition) {
		return windX[(int) World.wrapX(atPosition.x + windZOffset.x)][(int) World.wrapY(atPosition.z + windZOffset.y)];
	}
	
	public void regenerate() {
		windX = Noise.generate(Simulation.WORLD_SIZE_X, Simulation.WORLD_SIZE_Y, 0.5f);
		windZ = Noise.generate(Simulation.WORLD_SIZE_X, Simulation.WORLD_SIZE_Y, 0.5f);		
	}
}
