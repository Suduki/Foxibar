package actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import skills.SkillSet;

public class SkillSetTest {
	@Before
	public void before() {
		SkillSet.init();
	}
	
	@Test
	public void shouldNormalizeSkillVectorAfterInheritRandom() {
		SkillSet skillSet = new SkillSet();
		
		skillSet.inheritRandom();
		
		Assert.assertTrue("Expected skillSum to be 1, it was " + skillSet.sum(), 
				Math.abs(skillSet.sum() - 1) < 0.0001f);
	}
	
	@Test
	public void shouldNormalizeSkillVectorAfterMultipleInherits() {
		SkillSet skillSet = new SkillSet();
		
		skillSet.inheritRandom();
		
		for (int i = 0; i < 50; ++i) {
			skillSet.inherit(skillSet);
			Assert.assertTrue("Expected skillSum to be 1, it was " + skillSet.sum(), 
					Math.abs(skillSet.sum() - 1) < 0.0001f);
		}
	}
}
