package gui;

import static org.lwjgl.opengl.GL11.*;

public class HorizontalSplitRegion extends SplitRegion implements Region{
	public static final int TopIndex  = 0;
	public static final int BottomIndex = 1;

	public HorizontalSplitRegion() {
		super(Horizontal);
	}
	
	public HorizontalSplitRegion(Region pTopRegion, Region pBottomRegion) {
		super(pTopRegion, pBottomRegion, Horizontal);
	}
	
	public void setTopSubRegion(Region subRegion) {
		mSubRegions[TopIndex] = subRegion;
		updateGeometry();
	}
	
	public void setBottomSubRegion(Region subRegion) {
		mSubRegions[BottomIndex] = subRegion;
		updateGeometry();
	} 
	

	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		super.updateGeometry(pPosX, pPosY, pWidth, pHeight);
		
		if (mSubRegions[TopIndex] != null) {
			mSubRegions[TopIndex].updateGeometry(mPos.x, mPos.y, mSize.x, (int)(mSize.y*mDividerPosition));
		}
		if (mSubRegions[BottomIndex] != null) {
			mSubRegions[BottomIndex].updateGeometry(mPos.x, mPos.y + (int)(mSize.y*mDividerPosition), mSize.x, (int)(mSize.y*(1.0-mDividerPosition)));
		}
	}
		
	@Override public void renderDivider(GuiRenderer pGuiRenderer) {
		if (mMouseOverDivider) {
			glLineWidth(1 + 2*mDividerNeighbourhood);
		}
		else {
			glLineWidth(3.0f);
		}
		glBegin(GL_LINES);
		glVertex2i(mPos.x,           (int)(mPos.y + mSize.y*mDividerPosition));
		glVertex2i(mPos.x + mSize.x, (int)(mPos.y + mSize.y*mDividerPosition));
		glEnd();
		glLineWidth(1.0f);
	}
}
