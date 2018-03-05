package gui;

import static org.lwjgl.opengl.GL11.*;

public class DummyRegion extends AbstractRegion {

	private static int sNextId = 1;
	private static int nextId() {
		return sNextId++;
	}
	
	public DummyRegion() {
		mId = nextId();
	}
	
	private boolean mMouseInside = false;
	private int mId;
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		if (mMouseInside) {
			glColor3f(1,0,0);
		}
		glBegin(GL_LINES);
		glVertex2i(mPos.x,           mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y);
		
		glVertex2i(mPos.x + mSize.x, mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y + mSize.y);
		
		glVertex2i(mPos.x + mSize.x, mPos.y + mSize.y);
		glVertex2i(mPos.x,           mPos.y + mSize.y);
		
		glVertex2i(mPos.x,           mPos.y + mSize.y);
		glVertex2i(mPos.x,           mPos.y);
		
		glVertex2i(mPos.x,           mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y + mSize.y);
		
		glVertex2i(mPos.x + mSize.x, mPos.y);
		glVertex2i(mPos.x,           mPos.y + mSize.y);
		glEnd();
		
		glColor3f(1,1,1);
		return true;
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pState) {
		switch (pEvent) {
		case ENTER:
			mMouseInside = true;
			break;
		case LEAVE:
			mMouseInside = false;
			break;
		case BUTTON:
			//System.out.println("DummyRegion[" + mId + "]: Button " + pState.getButtonIndex() + (pState.getButtonState(pState.getButtonIndex()) ? " pressed." : " released."));
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}
