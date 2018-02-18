package gui;

import static org.lwjgl.opengl.GL11.*;

public class SceneRegion extends AbstractRegion {

	private SceneRegionRenderer mRenderer;
	
	public SceneRegion(SceneRegionRenderer pRenderer) {
		mRenderer = pRenderer;
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		if (mRenderer != null) {
			glPushAttrib(GL_VIEWPORT_BIT);
			int y = getWindow().getHeight();
			glViewport(mPos.x, y - mPos.y - mSize.y, mSize.x, mSize.y);
			glEnable(GL_SCISSOR_TEST);
			glScissor(mPos.x, y - mPos.y - mSize.y, mSize.x, mSize.y);
			mRenderer.render(mSize.x, mSize.y);
			glDisable(GL_SCISSOR_TEST);
			glDisable(GL_DEPTH_TEST);
			glPopAttrib();
		}
		
		return true;
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (mRenderer != null) {
			if (pMouse.getButtonState(pMouse.getButtonIndex())) {
				pMouse.setKeyboardFocusCandidate(this);
			}
			
			mRenderer.handleMouseEvent(pEvent, pMouse);
		}
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pKeyboard) {
		if (mRenderer != null) {
			mRenderer.handleKeyboardEvent(pKeyboard);
		}
		return true;
	}
}
