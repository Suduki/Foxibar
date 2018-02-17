package gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import gpu.GpuUtils;

public class GuiRenderer {

	public void setView(int pX, int pY, int pWidth, int pHeight) {
		glUseProgram(0);
		glDisable(GL_TEXTURE_2D);
		glViewport(pX, pY, pWidth, pHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, pWidth, pHeight, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
}
