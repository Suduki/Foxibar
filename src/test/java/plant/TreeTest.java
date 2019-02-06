package plant;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import testUtils.TestWithSimulation;

public class TreeTest extends TestWithSimulation {
	
	private Tree sut;
	private PlantManager manager;
	
	@Before
	public void beforeTest() {
		manager = new PlantManager(simulation.vision);
		sut = manager.spawnTree();
	}

	@Test
	public void testThatTreesGrow() {
		Assert.
		sut.stepAgent();
	}
	
	@Test
	public void test

}
