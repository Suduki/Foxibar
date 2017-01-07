package buttons;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.Position;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import constants.Constants;
import display.DisplayHandler;
import static constants.Constants.*;

public abstract class Button {
	private Vector2f position;
	protected Vector2f size;
	protected Texture texture;
	protected String texturePath = "/pics/defaultButton.png";
	protected static long lastClicked = 0;
	
	private static ArrayList<Button> allButtons = new ArrayList<Button>();
	
	public Button(Vector2f position) {
		super();
		size = new Vector2f(50f, 30f);
		this.position = position;
		texture = null;
		allButtons.add(this);
	}
	
	public static void updateAllButtons() {
		for (Button b : allButtons) {
			b.render();
			if (Mouse.isButtonDown(0) && b.insideBounds(Mouse.getX(), Constants.PIXELS_Y - Mouse.getY())) {
				if (Math.abs(lastClicked - System.currentTimeMillis()) > 500) {
					lastClicked = System.currentTimeMillis();
					b.onClick();
				}
			}
		}
	}
	
	public abstract void onClick();
	
	public boolean insideBounds(float x, float y) {
		return (x >= position.x - size.x && x < position.x + size.x
				&& y >= position.y - size.y && y < position.y + size.y);
	}
	
	public void render(){
		if (texture == null) {
			try {
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(
						texturePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Color.white.bind();
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0f, 0f);
		GL11.glVertex2f(position.x - (size.x), position.y - (size.y));
		GL11.glTexCoord2f(1f, 0f);
		GL11.glVertex2f(position.x + (size.x), position.y - (size.y));
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex2f(position.x + (size.x), position.y + (size.y));
		GL11.glTexCoord2f(0f, 1f);
		GL11.glVertex2f(position.x - (size.x), position.y + (size.y));
		GL11.glEnd();
		GL11.glPopMatrix();
		
	}
	
	public static void initAll() {
		float stdWidth = 50f, stdHeight = 30f;
		float[] stdPosX = new float[5];
		float[] stdPosY = new float[5];
		
		for (int i = 0; i < 5; ++i) {
			stdPosX[i] = PIXELS_X + 120f*(i+1);
			stdPosY[i] = PIXELS_Y - 80f*(i+1);
		}
		
		new ButtonKillAll(new Vector2f(stdPosX[0], stdPosY[0]));
		new ButtonRenderAnimals(new Vector2f(stdPosX[1], stdPosY[1]));
		new RegenerateWorld(new Vector2f(stdPosX[1], stdPosY[2]));
		new RenderVision(new Vector2f(stdPosX[0], stdPosY[1]));
	}
	
	
//	public void render() {
////		if (texture == null) {
//			try {
//				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(
//						texturePath));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
////		}
//		DisplayHelper.renderTexture(texture,
//				new float[] {position.x + size.x, position.x + size.x, position.x - size.x, position.x - size.x},  
//				new float[] {position.y - size.y, position.y + size.y, position.y + size.y, position.y - size.y}, 4);
//	}
	void hide() {
		
	}

}
