package skills;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

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
		SkillSet ancestorSS = new SkillSet();
		SkillSet inheritedSkillSet = new SkillSet();
		
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
		SkillSet ancestorSS = new SkillSet();
		SkillSet inheritedSkillSet = new SkillSet();
		
		ancestorSS.inheritRandom();
		inheritedSkillSet.inherit(ancestorSS);
		
		for (int i = 0 ; i < SkillSet.NUM_SKILLS; ++i) {
			Assert.assertTrue(inheritedSkillSet.skillsRelative[i] != ancestorSS.skillsRelative[i]);
		}
	}
	
	@Test
	public void shouldSetProperRanges() {
		SkillSet skillSet = new SkillSet();
		for (int numTries = 0; numTries < 50; ++numTries) {
			
		skillSet.inheritRandom();
		
			for (int i = 0; i < SkillSet.NUM_SKILLS; ++i) {
				if (SkillSet.RANGES[i][0] < SkillSet.RANGES[i][1]) {
					Assert.assertTrue(skillSet.skillsActual[i] >= SkillSet.RANGES[i][0]);
					Assert.assertTrue(skillSet.skillsActual[i] <= SkillSet.RANGES[i][1]);
				}
				else {
					Assert.assertTrue(skillSet.skillsActual[i] <= SkillSet.RANGES[i][0]);
					Assert.assertTrue(skillSet.skillsActual[i] >= SkillSet.RANGES[i][1]);
				}
			}
		}
	}
	
	@Test
	public void shouldNormalizeZero() {
		SkillSet skillSet = new SkillSet();
		for (int i = 0 ; i < SkillSet.NUM_SKILLS; ++i) {
			skillSet.skillsRelative[i] = 0;
		}
		skillSet.normalize();
		
		for (int i = 0 ; i < SkillSet.NUM_SKILLS; ++i) {
			Assert.assertTrue(skillSet.skillsActual[i] != Float.NaN);
		}
	}
	
}
