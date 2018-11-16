package talents;

import constants.Constants;
import display.Circle;

public class SkillsRenderCircle {
	
	private Talents talents;
	
	private TalentsCircularButton[] buttons;
	
	private Circle innerCircle, outerCircle, middleCircle;
	
	public SkillsRenderCircle(int outerRadius, int innerRadius) {
		this.talents = new Talents();
		talents.inheritRandom();
		
		innerCircle = new Circle(Talents.NUM_TALENTS, innerRadius, Constants.Colors.DesignYourAnimal.INNER);
		outerCircle = new Circle(Talents.NUM_TALENTS, outerRadius, Constants.Colors.DesignYourAnimal.OUTER);
		middleCircle = new Circle(Talents.NUM_TALENTS, outerRadius, Constants.Colors.DesignYourAnimal.MIDDLE);
		
		buttons = new TalentsCircularButton[Talents.NUM_TALENTS];
		for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
			buttons[i] = new TalentsCircularButton(i, talents);
		}
	}

	public void update(float middleX, float middleY) {
		innerCircle.setPos(middleX, middleY);
		outerCircle.setPos(middleX, middleY);
		middleCircle.setPos(middleX, middleY);
	}
	
	public void render() {
		outerCircle.renderCircle(1f);
		outerCircle.drawLinesAround();
		middleCircle.renderCircle(1f, talents.talentsRelative);
		middleCircle.drawLinesRadial(talents.talentsRelative);
		innerCircle.renderCircle(1f);
		innerCircle.drawLinesAround();
		
		for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
			buttons[i].render(middleCircle.getScaledXAt(i, talents.talentsRelative), middleCircle.getScaledYAt(i, talents.talentsRelative));
		}
		
	}
}
