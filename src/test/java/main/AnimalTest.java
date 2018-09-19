package main;

import static org.junit.Assert.*;

import org.junit.Test;

import agents.Brainler;
import constants.Constants;

public class AnimalTest {

	@Test
	public void ageTest() {
		Brainler a = new Brainler(0, null, null);
		a.age = 0;
		a.trueAge = 0;
		a.isAlive = true;
		assertTrue(a.age());
		verifyAge(a);
		
		a.trueAge = 0;
		a.isAlive = true;
		assertTrue(a.age());
		verifyAge(a);

	}
	
	private void verifyAge(Brainler a) {
		assertTrue(a.age > 0);
		assertTrue(a.trueAge > 0);
		assertTrue(a.isAlive == true);
	}

	
	@Test
	public void testMockito() {
		System.out.println("asd");
		
	}
}
