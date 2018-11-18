package talents;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import constants.Constants;
import display.Circle;

public class SkillsRenderCircle {
	
	private Talents talents;
	
	private TalentsCircularButton[] buttons;
	
	private Circle innerCircle, outerCircle, middleCircle;
	
	public SkillsRenderCircle(int outerRadius, int innerRadius, Talents talents) {
		this.talents = talents;
		
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
