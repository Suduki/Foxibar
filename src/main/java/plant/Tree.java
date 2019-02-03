package plant;

import java.util.ArrayList;

import org.joml.Vector2f;

import agents.Agent;
import agents.Animal;
import vision.Vision;
import world.World;

public class Tree  extends Agent {
	
	public float leafness;

	public Tree() {
		super();
	}

	@Override
	public boolean stepAgent() {
		return false;
	}
}
