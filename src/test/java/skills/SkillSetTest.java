package skills;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import talents.Talents;

public class SkillSetTest {
	@Before
	public void before() {
		Talents.init();
	}
	
	@Test
	public void shouldNormalizeSkillVectorAfterInheritRandom() {
		Talents skillSet = new Talents();
		
		skillSet.inheritRandom();
		
		Assert.assertTrue("Expected skillSum to be 1, it was " + skillSet.sum(), 
				Math.abs(skillSet.sum() - 1) < 0.0001f);
	}
	
	@Test
	public void shouldNormalizeSkillVectorAfterMultipleInherits() {
		Talents ancestorSS = new Talents();
		Talents inheritedSkillSet = new Talents();
		
		ancestorSS.inheritRandom();
		inheritedSkillSet.inherit(ancestorSS);
		
		for (int i = 0; i < 50; ++i) {
			inheritedSkillSet.inherit(inheritedSkillSet);
			Assert.assertTrue("Expected skillSum to be 1, it was " + inheritedSkillSet.sum(), 
					Math.abs(inheritedSkillSet.sum() - 1f) < 0.0001f);
		}
	}
	
	@Test
	public void shouldMutateAfterInherit() {
		Talents ancestorSS = new Talents();
		Talents inheritedSkillSet = new Talents();
		
		ancestorSS.inheritRandom();
		inheritedSkillSet.inherit(ancestorSS);
		
		for (int i = 0 ; i < Talents.NUM_TALENTS; ++i) {
			Assert.assertTrue(inheritedSkillSet.talentsRelative[i] != ancestorSS.talentsRelative[i]);
		}
	}
	
	@Test
	public void shouldSetProperRanges() {
		Talents skillSet = new Talents();
		for (int numTries = 0; numTries < 50; ++numTries) {
			
		skillSet.inheritRandom();
		
			for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
				if (Talents.RANGES[i][0] < Talents.RANGES[i][1]) {
					Assert.assertTrue(skillSet.talentsActual[i] >= Talents.RANGES[i][0]);
					Assert.assertTrue(skillSet.talentsActual[i] <= Talents.RANGES[i][1]);
				}
				else {
					Assert.assertTrue(skillSet.talentsActual[i] <= Talents.RANGES[i][0]);
					Assert.assertTrue(skillSet.talentsActual[i] >= Talents.RANGES[i][1]);
				}
			}
		}
	}
	
	@Test
	public void shouldNormalizeZero() {
		Talents skillSet = new Talents();
		for (int i = 0 ; i < Talents.NUM_TALENTS; ++i) {
			skillSet.talentsRelative[i] = 0;
		}
		skillSet.normalize();
		
		for (int i = 0 ; i < Talents.NUM_TALENTS; ++i) {
			Assert.assertTrue(skillSet.talentsActual[i] != Float.NaN);
		}
	}
	
}
