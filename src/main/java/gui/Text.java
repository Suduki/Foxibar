package gui;

import static org.lwjgl.opengl.GL11.*;

public class Text {
	private String mText = "";
	private float[] mData = null;
	private float mWidth = 0;
	private float mHeight = 0;
	
	// <DIAGNOSTICS>
	private boolean mDrawDiagnostics = false;
	private float[] mCursors = null;
	private int mIndex = 0;
	// </DIAGNOSTICS>
	
	public Text(String pText) {
		mText = pText;
		mData = new float[16*pText.length()];
		mCursors = new float[pText.length()];
		
		Font font = Font.defaultFont();
		
		float cursor = 0;
		int i = 0;
		for (char ch : pText.toCharArray()) {
			//System.out.println("Char: " + ch + " = " + (int)ch);

			
			Font.CharacterDefinition def = font.getCharacterDefinition((int)ch);
			if (def != null) {
				pushChar(def, cursor);
				cursor += def.xAdvance;
			}
			mCursors[i] = cursor;
			++i;
		}
		mWidth = cursor;
		mHeight = 1; // TODO: Support multi-line.
	}
	
	int getWidth(float pFontSize) {
		return (int)(pFontSize*mWidth);
	}
	
	public int getHeight(float pFontSize) {
		return (int)(pFontSize*mHeight);
	}
	
	public void draw(float x, float y, float size) {
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		Font.defaultFont().getTexture().bind(0);
		glBegin(GL_QUADS);
		for (int i = 0; i < mData.length; i+=4) {
			glTexCoord2f(mData[i+2], mData[i+3]);
			glVertex2f(x+size*mData[i+0], y+size*mData[i+1]);
		}
		glEnd();
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		
		if (mDrawDiagnostics) {
			drawDiagnostics(x,y,size);
		}
	}
	
	public void drawCentered(float x, float y, float size) {
		float sizeX = size*mWidth;
		float sizeY = size*mHeight;
		draw(x - sizeX/2, y - sizeY/2, size);
	}
	
	private void drawDiagnostics(float x, float y, float size) {
		float width = getWidth(size);
		float height = getHeight(size);
		glPushAttrib(GL_CURRENT_BIT);
		glBegin(GL_LINES);
		glColor3f(1,0,0);
		glVertex2f(x,y+height);
		glVertex2f(x+width, y+height);
		glColor3f(1,1,0);
		glVertex2f(x,y);
		glVertex2f(x+width, y);
		glVertex2f(x,y);
		glVertex2f(x, y+height);
		for (int i = 0; i < mCursors.length; ++i) {
			glVertex2f(x + mCursors[i]*size, y);
			glVertex2f(x + mCursors[i]*size, y+height);
		}
		glEnd();
		
		glColor3f(0,1,1);
		for (int j = 0; j < mData.length; j+=16) {
			glBegin(GL_LINE_LOOP);
			for (int i = j; i < j+16; i+=4) {
				glTexCoord2f(mData[i+2], mData[i+3]);
				glVertex2f(x+size*mData[i+0], y+size*mData[i+1]);
			}
			glEnd();
		}
		glPopAttrib();
	}
	
	private void pushChar(Font.CharacterDefinition def, float cursor) {
		pushVertex(cursor         + def.xOffset, 0     + def.yOffset,     def.u0, def.v0);
		pushVertex(cursor + def.w + def.xOffset, 0     + def.yOffset,     def.u1, def.v0);
		pushVertex(cursor + def.w + def.xOffset, def.h + def.yOffset, def.u1, def.v1);
		pushVertex(cursor         + def.xOffset, def.h + def.yOffset, def.u0, def.v1);
	}
	
	private void pushVertex(float x, float y, float u, float v) {
		mData[mIndex++] = x;
		mData[mIndex++] = y;
		mData[mIndex++] = u;
		mData[mIndex++] = v;
	}
}
