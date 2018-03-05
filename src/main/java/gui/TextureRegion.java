package gui;

import static org.lwjgl.opengl.GL11.*;

import display.Texture;

public class TextureRegion extends AbstractRegion {

	private Texture mTexture;
	private float mLeftU;
	private float mRightU;
	private float mTopV;
	private float mBottomV;
	
	public TextureRegion(Texture pTexture, float pLeftU, float pRightU, float pTopV, float pBottomV) {
		mTexture = pTexture;
		mLeftU   = pLeftU;
		mRightU  = pRightU;
		mTopV    = pTopV;
		mBottomV = pBottomV;
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		
		if (mTexture != null ) {
			glEnable(GL_TEXTURE_2D);
			mTexture.bind(0);
		}
		
		glBegin(GL_QUADS);
		glTexCoord2f(mLeftU, mTopV);
		glVertex2i(mPos.x,           mPos.y + mSize.y);
		
		glTexCoord2f(mRightU, mTopV);
		glVertex2i(mPos.x + mSize.x, mPos.y + mSize.y);
		
		glTexCoord2f(mRightU, mBottomV);
		glVertex2i(mPos.x + mSize.x, mPos.y);
		
		glTexCoord2f(mLeftU, mBottomV);
		glVertex2i(mPos.x,           mPos.y);
		glEnd();
		
		glDisable(GL_TEXTURE_2D);
		
		return true;
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pState) {
		// TODO Auto-generated method stub
		return false;
	}
}
