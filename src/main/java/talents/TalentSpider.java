package talents;

import static org.lwjgl.opengl.GL11.glColor4f;

import agents.Brainler;
import constants.Constants;
import display.Circle;
import gui.Text;

public class TalentSpider {
	
	private Brainler brainler;
	
	private TalentsCircularButton[] buttons;
	
	private Circle innerCircle, outerCircle, middleCircle;
	
	private Text[] texts;
	
	public TalentSpider(int outerRadius, int innerRadius, Brainler brainler) {
		this.brainler = brainler;
		
		innerCircle = new Circle(Talents.NUM_TALENTS, innerRadius, brainler.color);
		outerCircle = new Circle(Talents.NUM_TALENTS, outerRadius, brainler.secondaryColor);
		middleCircle = new Circle(Talents.NUM_TALENTS, outerRadius, brainler.color);
		buttonPositions = new float[Talents.NUM_TALENTS];
		
		texts = new Text[Talents.NUM_TALENTS];
		
		buttons = new TalentsCircularButton[Talents.NUM_TALENTS];
		for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
			buttons[i] = new TalentsCircularButton(i, brainler.talents);
			texts[i] = new Text(Talents.names[i]);
		}

	}

	public void update(float middleX, float middleY) {
		innerCircle.setPos(middleX, middleY);
		outerCircle.setPos(middleX, middleY);
		middleCircle.setPos(middleX, middleY);
	}
	
	private float[] buttonPositions;
	public void render() {
		
		for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
			buttonPositions[i] = 0.5f * brainler.talents.talentsRelative[i] + 0.5f;
		}
		
		outerCircle.renderCircle(1f);
		outerCircle.drawBorder();
		middleCircle.renderCircle(0.5f, buttonPositions);
		middleCircle.drawLinesRadial(buttonPositions);
		innerCircle.renderCircle(1f);
		innerCircle.drawBorder();

		float[] textColor = Constants.Colors.WHITE; 
		glColor4f(textColor[0], textColor[1], textColor[2], 1f);
		for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
			buttons[i].render(middleCircle.getScaledXAt(i, buttonPositions[i]), 
					middleCircle.getScaledZAt(i, buttonPositions[i]));
			
			float textsX = outerCircle.getScaledXAt(i, 1.4f);
			float textsY = outerCircle.getScaledZAt(i, 1.2f);
			texts[i].drawCentered(textsX, textsY, innerCircle.radius / 5);
		}
		
	}
	
	public int isInsideButton(float x, float y) {
		for (int i = 0; i < Talents.NUM_TALENTS; ++i) {
			System.out.println(buttons[i].circle.position.x + " " + buttons[i].circle.position.y + " pressed in SkillsRenderCircle");
			if (buttons[i].circle.isInside(x, y)) {
				System.out.println(i + " pressed in SkillsRenderCircle");
				return i;
			}
		}
		return -1;
	}
	
}
