package gui;

import constants.Constants;
import simulation.Simulation;
import display.InputHandlerI;
import display.Window;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GuiRoot implements InputHandlerI, RegionI {
	private Window				mWindow					= null;
	private RegionI				mRootRegion				= null;
	private RegionI				mKeyboardFocusRegion	= null;
	private GuiRenderer			mGuiRenderer			= null;
	private MouseStateImpl		mMouseState				= null;
	private KeyboardStateImpl	mKeyboardState			= null;
	private Simulation			mSimulation				= null;
	
	public GuiRoot(Window pWindow, Simulation pSimulation) {
		mWindow = pWindow;
		mWindow.setInputHandler(this);
		mGuiRenderer   	= new GuiRenderer();
		mMouseState    	= new MouseStateImpl();
		mKeyboardState 	= new KeyboardStateImpl();
		mSimulation		= pSimulation;
	}
		
	public void setRootRegion(RegionI pRootRegion) {
		mRootRegion = pRootRegion;
		
		if (mRootRegion != null) {
			mRootRegion.setParent(this);
			mRootRegion.updateGeometry(0, 0, mWindow.getWidth(), mWindow.getHeight());
		}
	}
	
	public void render() {		
		mGuiRenderer.setView(0, 0, mWindow.getWidth(), mWindow.getHeight());
		glClearColor(.3f, .3f, .3f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		if (mRootRegion != null) {
			mRootRegion.render(mGuiRenderer);
		}
	}
	
	
	@Override
	public void handleKeyboardEvents(int action, int key) {
		if (action != GLFW_REPEAT) {
			mKeyboardState.setKeyState(key, action == GLFW_PRESS);
			
			if (mKeyboardFocusRegion != null) {
				mKeyboardFocusRegion.handleKeyboardEvent(mKeyboardState);
			}
			handleCommonKeyboardReleaseEvents(action, key);
		}
	}
	
	private void handleCommonKeyboardReleaseEvents(int action, int key) {
		if (action == GLFW_RELEASE) {
			switch (key) {

			case GLFW_KEY_SPACE:
				System.out.println("KEY_SPACE released, handled in handleCommonKeyboardReleaseEvents");
				mSimulation.message(new messages.PauseSimulation());
				break;

			case GLFW_KEY_R:
//				mSimulation.zoomFactor = 1.0f;
//				mSimulation.x0 = 0;
//				mSimulation.y0 = 0;
				break;

			case GLFW_KEY_2:
				System.out.println("KEY_2 released, handled in handleCommonKeyboardReleaseEvents");
				utils.FPSLimiter.mWantedFps /= 2;
				break;

			case GLFW_KEY_1:
				System.out.println("KEY_1 released, handled in handleCommonKeyboardReleaseEvents");
				utils.FPSLimiter.mWantedFps *= 2;
				break;

			case GLFW_KEY_3:
				System.out.println("KEY_3 released, handled in handleCommonKeyboardReleaseEvents");
				utils.FPSLimiter.mWantedFps = Constants.WANTED_FPS;
				break;
			}
		}
	}

	@Override
	public void handleMouseEvents(long window, int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			mMouseState.setButtonState(button, true);
		}
		else {
			mMouseState.setButtonState(button, false);
		}
		
		if (mRootRegion == null) {
			return;
		}
		
		mRootRegion.handleMouseEvent(MouseEvent.BUTTON, mMouseState);
		
		RegionI candidate = mMouseState.getKeyboardFocusCandidate(); 
		mMouseState.resetKeyboardFocusCandidate();		
		if (mKeyboardFocusRegion != candidate) {			
			if (mKeyboardFocusRegion != null) {
				mKeyboardFocusRegion.keyboardFocusRevoked();
			}
			
			if (candidate != null && candidate.keyboardFocusGranted()) {
				mKeyboardFocusRegion = candidate;
			}
			else {
				mKeyboardFocusRegion = null;
			}
		}		
	}
	
	@Override
	public void handleMouseMotion(long window, double xpos, double ypos) {
		mMouseState.setPos(new Point((int)xpos, (int)ypos));
		
		if (mRootRegion == null) {
			return;
		}
		
		boolean wasInsideRoot = mRootRegion.isPointInside(mMouseState.getOldPos());
		Point delta = wasInsideRoot ? mMouseState.getPos().minus(mMouseState.getOldPos()) : new Point(0,0);
		mMouseState.setDelta(delta);

		if (mRootRegion.didMouseEnter(mMouseState)) {
			mRootRegion.handleMouseEvent(MouseEvent.ENTER, mMouseState);			
		}
		else if (mRootRegion.didMouseLeave(mMouseState)) {
			mRootRegion.handleMouseEvent(MouseEvent.LEAVE, mMouseState);
		}
		
		boolean isInsideRoot = mRootRegion.isPointInside(mMouseState.getPos());
		if (isInsideRoot) {
			mRootRegion.handleMouseEvent(MouseEvent.MOTION, mMouseState);
		}
	}
	
	@Override
	public void handleScrollWheel(long window, double xoffset, double yoffset) {
		System.out.println("SCROLLI'N! xoffset = " + xoffset + " yoffset = " + yoffset);
	}

	@Override
	public void handleFramebufferSize(long window, int width, int height) {
		if (mRootRegion != null) {
			mRootRegion.updateGeometry(0, 0, width, height);
		}		
	}

	@Override
	public Point getPos() {
		return new Point(0,0);
	}

	@Override
	public Point getSize() {
		if (mWindow != null) {
			return new Point(mWindow.getWidth(), mWindow.getHeight());
		}
		
		return null;
	}
	
	@Override
	public RegionI getParent() {
		return null;
	}
	
	@Override
	public Window getWindow() {
		return mWindow;
	}

	@Override public boolean render(GuiRenderer pGuiRenderer) {return false;} // TODO Auto-generated method stub
	@Override public boolean handleMouseEvent(MouseEvent pEvent, MouseState pState) {return false;} // TODO Auto-generated method stub
	@Override public boolean handleKeyboardEvent(KeyboardState pState) {return false;} // TODO Auto-generated method stub
	@Override public void    updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) { } // TODO Auto-generated method stub
	@Override public boolean isPointInside(Point pPoint) {return false;} // TODO Auto-generated method stub
	@Override public boolean didMouseEnter(MouseState pState) {return false;} // TODO Auto-generated method stub
	@Override public boolean didMouseLeave(MouseState pState) {return false;} // TODO Auto-generated method stub
	@Override public boolean hasKeyboardFocus() {return false;} // TODO Auto-generated method stub
	@Override public boolean keyboardFocusGranted() {return false;} // TODO Auto-generated method stub
	@Override public void    keyboardFocusRevoked() {} // TODO Auto-generated method stub
	@Override public void    setParent(RegionI pParent) {} // TODO Auto-generated method stub

	@Override
	public Point minSize() {
		return new Point();
	}

}
