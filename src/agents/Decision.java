package agents;

import constants.Constants;

public class Decision {
	
	public static final Decision STANDARD_GRASSLER = new Decision(10f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
	public static final Decision STANDARD_BLOODLING = new Decision(10f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
	
	public float wantToMateness;
	public float wantToFleeness;
	public float wantToKillness;
	public float wantToBeAloneness;
	public float wantHighGrass;
	public float wantHighBlood;
	
	public Decision(float wantToMateness, float wantToFleeness, float wantToKillness, float wantToBeAloneness, 
			float wantHighGrass, float wantHighBlood) {
		this.wantToMateness = wantToMateness;
		this.wantToFleeness = wantToFleeness;
		this.wantToKillness = wantToKillness;
		this.wantToBeAloneness = wantToBeAloneness;
		this.wantHighGrass = wantHighGrass;
		this.wantHighBlood = wantHighBlood;
	}
	
	public Decision(Decision d) {
		this.inherit(d, d);
	}

	public void inherit(Decision mom, Decision dad) {
		float evolution = 0.01f;
		this.wantToMateness    = (mom.wantToMateness + dad.wantToMateness)*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		this.wantToFleeness    = (mom.wantToFleeness + dad.wantToFleeness)*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		this.wantToKillness    = (mom.wantToKillness + dad.wantToKillness)*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		this.wantToBeAloneness = (mom.wantToBeAloneness + dad.wantToBeAloneness)*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		this.wantHighGrass     = (mom.wantHighGrass + dad.wantHighGrass)*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		this.wantHighBlood     = (mom.wantHighBlood + dad.wantHighBlood)*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
	}
	
	public void randomize() {
		this.wantToMateness    = Constants.RANDOM.nextFloat();
		this.wantToFleeness    = Constants.RANDOM.nextFloat();
		this.wantToKillness    = Constants.RANDOM.nextFloat();
		this.wantToBeAloneness = Constants.RANDOM.nextFloat();
		this.wantHighGrass     = Constants.RANDOM.nextFloat();
		this.wantHighBlood     = Constants.RANDOM.nextFloat();
	}
	
}
