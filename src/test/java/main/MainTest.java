package main;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import agents.Brainler;


public class MainTest {
	
	@Test
	public void speciesTest() {
		Brainler a = new Brainler(0, null, null);
		a.inherit(null);
		Brainler b = new Brainler(0, null, null);
		
		b.inherit(null);
		Assert.assertFalse(a.isCloselyRelatedTo(b));
		
		b.inherit(a);
		Assert.assertTrue(a.isCloselyRelatedTo(b));
		
		for (int gen = 0; gen < 20; gen++) {
			b.inherit(b);
			System.out.println(a.findRelationTo(b));
		}
		Assert.assertFalse(a.isCloselyRelatedTo(b));
	}
	

	/////////////
	// HELPERS //
	/////////////

}
