package display;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.joml.Vector3f;

import constants.Constants;

class OrbitCameraController implements InputHandlerI {

	private float mAngleX = 0;
	private float mAngleY  = 0;
	private float mDistance = 5;
	private boolean mIsOrbiting = false;
	private double mLastX = 0;
	private double mLastY = 0;
	private Camera mCamera = null;
	private Vector3f mPosition = null;
	
	OrbitCameraController(Camera pCamera) {
		mCamera = pCamera;
		mDistance = 75.0f;//mCamera.getEyePosition().distance(mCamera.getTargetPosition());
		mPosition = new Vector3f(0,100,0);
	}
	
	@Override
	public void handleKeyboardEvents(int action, int key) {
	}

	@Override
	public void handleMouseEvents(long window, int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			if (button == 0) {
				mIsOrbiting = true;
			}
		}
		else {
			if (button == 0) {
				mIsOrbiting = false;
			}
		}
	}

	@Override
	public void handleMouseMotion(long window, double xpos, double ypos) {
		double dx = xpos - mLastX;
		double dy = ypos - mLastY;
		
		if (mIsOrbiting) {
			mAngleX = (float)Math.max(-Math.PI/4.0,  Math.min(Math.PI/2.0-0.01f, mAngleX + Math.toRadians(dy)));
			mAngleY += Math.toRadians(dx);

			update();
		}
		
		mLastX = xpos;
		mLastY = ypos;
	}

	@Override
	public void handleScrollWheel(long window, double xoffset, double yoffset) {	
		mDistance *= Math.pow(1.02, -yoffset);
		update();
	}
	
	private void update() {
		double x = mDistance*Math.cos(mAngleY)*Math.cos(-mAngleX);
		double z = mDistance*Math.sin(mAngleY)*Math.cos(-mAngleX);
		double y = -mDistance*Math.sin(-mAngleX);
		
		mCamera.setEyePosition(new Vector3f((float)x, (float)y+100, (float)z).add(mPosition));
		mCamera.setTargetPosition(mPosition);
	}

	@Override
	public void handleFramebufferSize(long window, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
}