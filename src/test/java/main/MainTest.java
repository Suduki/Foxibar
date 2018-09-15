package main;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import agents.Animal;


public class MainTest {
	
	@Test
	public void speciesTest() {
		Animal a = new Animal(0, null, null);
		a.inherit(null);
		Animal b = new Animal(0, null, null);
		
		b.inherit(null);
		Assert.assertFalse(a.isCloselyRelated(b));
		
		b.inherit(a);
		Assert.assertTrue(a.isCloselyRelated(b));
		
		for (int gen = 0; gen < 20; gen++) {
			b.inherit(b);
			System.out.println(a.findRelationTo(b));
		}
		Assert.assertFalse(a.isCloselyRelated(b));
	}
	

	/////////////
	// HELPERS //
	/////////////

}
