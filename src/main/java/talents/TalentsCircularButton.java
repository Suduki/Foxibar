package talents;

import org.joml.Vector2f;

import constants.Constants;
import display.Circle;

public class TalentsCircularButton {
	Vector2f home;
	Vector2f goal;
	Circle circle;
	private float distanceBetweenHomeAndGoal;
	private int talentId;
	
	public TalentsCircularButton(int talentId, Talents talents) {
		home = new Vector2f();
		goal = new Vector2f();
		circle = new Circle(20, 15, Constants.Colors.DesignYourAnimal.BUTTON);
		this.talentId = talentId;
	}

	public void render(float x, float y) {
		circle.setPos(x, y);
		circle.renderCircle(1f);
		circle.drawLinesAround();
	}
	
	public void update(float mousePosX, float mousePosY, Talents talents) {
		float distanceFromHome = home.distance(mousePosX, mousePosY);
		float scale = distanceFromHome / distanceBetweenHomeAndGoal;
		if (scale > 1f) {
			scale = 1f;
		}
		
		circle.setPos(home.x * scale + goal.x * (1f-scale), home.y * scale + goal.y * (1f-scale));
		
		talents.normalizeWithRespectTo(talentId);
	}
}
