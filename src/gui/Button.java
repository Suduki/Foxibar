package gui;

import static org.lwjgl.opengl.GL11.*;

public class Button extends AbstractRegion {
	public interface ClickCallback {
		void onClick();
	}
	
	private Text mText;
	private boolean mMouseInside = false;
	private boolean mClickStarted = false;
	private ClickCallback mCallback = null;
	private int mFontSize = 32;
	
	
	public Button(String pText) {
		mText = new Text(pText);
	}
	
	public Button(String pText, ClickCallback pCallback) {
		this(pText);
		setCallback(pCallback);
	}
	
	public void setCallback(ClickCallback pCallback) {
		mCallback = pCallback;
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		
		float s0 = 0.55f;
		float s1 = 0.70f;
		float s2 = 0.85f;
		if (mClickStarted) {
			glColor3f(s2,s2,s2);
		}
		else if (mMouseInside) {
			glColor3f(s1,s1,s1);
		}
		else {
			glColor3f(s0,s0,s0);
		}
		
		glBegin(GL_QUADS);
		glVertex2i(mPos.x,           mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y + mSize.y);
		glVertex2i(mPos.x,           mPos.y + mSize.y);
		glEnd();
		
		if (mClickStarted) {
			glColor3f(0,0,0);
		}
		else {
			glColor3f(1,1,1);
		}
		int dx = (mSize.x - mText.getWidth(mFontSize))/2;
		int dy = (mSize.y - mText.getHeight(mFontSize))/2;
		mText.draw(mPos.x+dx, mPos.y+dy, mFontSize);
		
		return true;
	}

	private void performClick(MouseState pMouse) {
		System.out.println("Button \"" + mText + "\" clicked!");
		if (mCallback != null) {
			mCallback.onClick();
		}
		mClickStarted = false;
	}
	
	private void cancelClick(MouseState pMouse) {
		System.out.println("Button \"" + mText + "\" click cancelled.");
		mClickStarted = false;
	}
	
	@Override
	public Point minSize() {
		int pad = mFontSize/4;
		return new Point(mText.getWidth(mFontSize) + pad, mText.getHeight(mFontSize) + pad);
	}
	
	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		int     index = pMouse.getButtonIndex();
		boolean state = pMouse.getButtonState(index);
		
		switch (pEvent) {
		case ENTER:
			mMouseInside = true;
			if (mClickStarted && !pMouse.getButtonState(0)) {
				cancelClick(pMouse);
			}
			break;
		case LEAVE:
			mMouseInside = false;
			break;
		case BUTTON:
			if (index == 0) {
				if (state) {
					mClickStarted = true;					
				}
				else if (mClickStarted) {
					performClick(pMouse);
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pState) {
		// TODO Auto-generated method stub
		return false;
	}

}
