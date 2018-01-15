package gpu.nodes;

import static org.lwjgl.opengl.GL11.*;

public class SetViewport extends gpu.RenderNode {

	private int mWidth   = 0;
	private int mHeight  = 0;
	private float mRed   = 0;
	private float mGreen = 0;
	private float mBlue  = 0;
	
	public SetViewport(int width, int height, float r, float g, float b) {
		mWidth  = width;
		mHeight = height;
		mRed    = r;
		mGreen  = g;
		mBlue   = b;
	}
	
	public void enter(gpu.Renderer renderer) {
		glViewport(0, 0, mWidth, mHeight);
		glClearColor(mRed, mGreen, mBlue, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
