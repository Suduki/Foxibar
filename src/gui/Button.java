package gui;

import static org.lwjgl.opengl.GL11.*;

public class Button extends AbstractRegion {
	public interface ClickCallback {
		void onClick();
	}
	
	private String mString = "";
	private Text mText;
	private boolean mMouseInside = false;
	private boolean mClickStarted = false;
	private ClickCallback mCallback = null;
	
	
	public Button(String pText) {
		mString = pText;
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
		
		if (mMouseInside) {
			glLineWidth(3.0f);
		}
		if (mClickStarted) {
			glColor3f(0,1,0);
		}
		glBegin(GL_LINE_LOOP);
		glVertex2i(mPos.x,           mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y);
		glVertex2i(mPos.x + mSize.x, mPos.y + mSize.y);
		glVertex2i(mPos.x,           mPos.y + mSize.y);
		glEnd();		
		glLineWidth(1.0f);

		int fontSize = 56;
		int dx = (mSize.x - mText.getWidth(fontSize))/2;
		int dy = (mSize.y - mText.getHeight(fontSize))/2;
		mText.draw(mPos.x+dx, mPos.y+dy, 56);
		glColor3f(1,1,1);
		
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
