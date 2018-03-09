package main;

import static org.junit.Assert.*;

import org.junit.Test;

import constants.Constants;
import static constants.Constants.*;

public class AnimalTest extends agents.Animal {

	//@Test
	public void ageTest() {
		AnimalTest a = new AnimalTest();
		a.species = Constants.Species.BLOODLING;
		a.age = 0;
		a.health = 0.1f;
		a.hunger = 10;
		a.isAlive = true;
		assertTrue(a.age());
		verifyAge(a);
		
		a.species = Constants.Species.GRASSLER;
		a.age = 0;
		a.health = 0.1f;
		a.hunger = 10;
		a.isAlive = true;
		assertTrue(a.age());
		verifyAge(a);

	}
	
	private void verifyAge(AnimalTest a) {
		assertTrue(a.age > 0);
		assertTrue(a.health > 0.1f);
		assertTrue(a.hunger < 10);
		assertTrue(a.isAlive == true);
	}

}
