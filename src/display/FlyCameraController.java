package display;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FlyCameraController implements InputHandlerI {

	private Camera mCamera;
	private double mLastX = 0;
	private double mLastY = 0;
	private float mAngleX = 0;
	private float mAngleY  = 0;
	private float mTargetAngleX = 0;
	private float mTargetAngleY = 0;
	private Vector3f mPosition = null;
	private Vector3f mTargetPosition = null;
	private boolean mIsRotating = false;
	private boolean mPressedW = false;
	private boolean mPressedS = false;
	private boolean mPressedA = false;
	private boolean mPressedD = false;
	private boolean mPressedQ = false;
	private boolean mPressedE = false;
	private boolean mPressedLeftShift = false;
	private boolean mPressedTab = false;
	FlyCameraController(Camera pCamera) {
		mCamera = pCamera;
		mPosition = new Vector3f(0, 100.0f, 0);
		mTargetPosition = new Vector3f(0, 100.0f, 0);
	}
	
	@Override
	public void handleKeyboardEvents(int action, int key) {
		if (action != GLFW_REPEAT) {
			switch(key) {
				case GLFW_KEY_W: mPressedW = (action==GLFW_PRESS); break;
				case GLFW_KEY_S: mPressedS = (action==GLFW_PRESS); break;
				case GLFW_KEY_A: mPressedA = (action==GLFW_PRESS); break;
				case GLFW_KEY_D: mPressedD = (action==GLFW_PRESS); break;
				case GLFW_KEY_Q: mPressedQ = (action==GLFW_PRESS); break;
				case GLFW_KEY_E: mPressedE = (action==GLFW_PRESS); break;
				case GLFW_KEY_LEFT_SHIFT: mPressedLeftShift = (action==GLFW_PRESS); break;
				case GLFW_KEY_TAB: mPressedTab = (action==GLFW_PRESS); break;
				default: break;
			}
		}
	}

	@Override
	public void handleMouseEvents(long window, int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			if (button == 0) {
				mIsRotating = true;
			}
		}
		else {
			if (button == 0) {
				mIsRotating = false;
			}
		}
	}

	@Override
	public void handleMouseMotion(long window, double xpos, double ypos) {
		double p = 0.25f;
		double dx = p*(xpos - mLastX);
		double dy = p*(ypos - mLastY);
		
		if (mIsRotating) {
			mTargetAngleX = (float)Math.max(-Math.PI/2.01,  Math.min(Math.PI/2.0-0.01f, mTargetAngleX - Math.toRadians(dy)));
			mTargetAngleY += Math.toRadians(dx);
		}
		
		mLastX = xpos;
		mLastY = ypos;
	}

	@Override
	public void handleScrollWheel(long window, double xoffset, double yoffset) {
	}

	public void update() {
		
		if (mPressedTab) {
			float r = 0.2f;
			mTargetAngleX = (float) (r*(-Math.PI/2.01) + (1.0f-r)*mTargetAngleX);
			mTargetAngleY = (1.0f-r*r)*mTargetAngleY;
			
			mTargetPosition = new Vector3f(mTargetPosition).mul(r*r).add(new Vector3f(0, 200, 0).mul(1.0f-r*r));
		}
		
		float dt = 1.0f/30.0f; // TODO: Get from somewhere more reliable.
		float speed = mPressedLeftShift ? 200.0f : 100;
		
		Matrix4f m = mCamera.getViewMatrix().invert();
		
		Vector4f ex = m.transform(new Vector4f(1.0f,0.0f,0.0f,0.0f));
		Vector4f ey = new Vector4f(0.0f,1.0f,0.0f,0.0f);
		Vector4f ez = m.transform(new Vector4f(0.0f,0.0f,1.0f,0.0f));
		
		Vector4f dx = ex.mul((mPressedD?1.0f:0.0f) - (mPressedA?1.0f:0.0f));
		Vector4f dy = ey.mul((mPressedE?1.0f:0.0f) - (mPressedQ?1.0f:0.0f));
		Vector4f dz = ez.mul((mPressedS?1.0f:0.0f) - (mPressedW?1.0f:0.0f));
		
		Vector4f dir4 = dx.add(dy.add(dz));
		Vector3f dir = new Vector3f(dir4.x, dir4.y, dir4.z);
		
		if (dir.length() > 0) {
			dir.normalize();
		}
		
		float p = 0.9f;
		float q = 0.5f;
		mTargetPosition.add(dir.mul(speed*dt));
		mPosition = new Vector3f(mPosition).mul(p).add(new Vector3f(mTargetPosition).mul(1.0f-p)); 
		
		mAngleX = q*mAngleX + (1.0f-q)*mTargetAngleX;
		mAngleY = q*mAngleY + (1.0f-q)*mTargetAngleY;
		
		double x = 75.0f*Math.cos(mAngleY)*Math.cos(-mAngleX);
		double z = 75.0f*Math.sin(mAngleY)*Math.cos(-mAngleX);
		double y = -75.0f*Math.sin(-mAngleX);
				
		Vector3f viewDir = new Vector3f((float)x, (float)y, (float)z);
		
		mCamera.setEyePosition(mPosition);
		mCamera.setTargetPosition(viewDir.add(mPosition));
	}
}
