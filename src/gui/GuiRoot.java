package gui;

import display.InputHandlerI;
import display.Window;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GuiRoot implements InputHandlerI, Region {
	private Window            mWindow              = null;
	private Region            mRootRegion          = null;
	private Region            mKeyboardFocusRegion = null;
	private GuiRenderer       mGuiRenderer         = null;
	private MouseStateImpl    mMouseState          = null;
	private KeyboardStateImpl mKeyboardState       = null;
	
	public GuiRoot(Window pWindow) {
		mWindow = pWindow;
		mWindow.setInputHandler(this);
		mGuiRenderer   = new GuiRenderer();
		mMouseState    = new MouseStateImpl();
		mKeyboardState = new KeyboardStateImpl();
	}
		
	public void setRootRegion(Region pRootRegion) {
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
		
		mRootRegion.handleMouseEvent(MouseEvent.BUTTON, mMouseState);
		
		Region candidate = mMouseState.getKeyboardFocusCandidate(); 
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
	public Region getParent() {
		return null;
	}
	
	@Override
	public Window getWindow() {
		return mWindow;
	}

	@Override public boolean render(GuiRenderer pGuiRenderer) {return false;} // TODO Auto-generated method stub
	@Override public boolean handleMouseEvent(MouseEvent pEvent, MouseState pState) {return false;} // TODO Auto-generated method stub
	@Override public boolean handleKeyboardEvent(KeyboardState pState) {return false;} // TODO Auto-generated method stub
	@Override public void    updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {} // TODO Auto-generated method stub
	@Override public boolean isPointInside(Point pPoint) {return false;} // TODO Auto-generated method stub
	@Override public boolean didMouseEnter(MouseState pState) {return false;} // TODO Auto-generated method stub
	@Override public boolean didMouseLeave(MouseState pState) {return false;} // TODO Auto-generated method stub
	@Override public boolean hasKeyboardFocus() {return false;} // TODO Auto-generated method stub
	@Override public boolean keyboardFocusGranted() {return false;} // TODO Auto-generated method stub
	@Override public void    keyboardFocusRevoked() {} // TODO Auto-generated method stub
	@Override public void    setParent(Region pParent) {} // TODO Auto-generated method stub

}
