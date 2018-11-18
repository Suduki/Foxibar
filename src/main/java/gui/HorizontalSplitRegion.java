package gui;

import static org.lwjgl.opengl.GL11.*;

public class HorizontalSplitRegion extends SplitRegion implements RegionI{
	public static final int TopIndex  = 0;
	public static final int BottomIndex = 1;

	public HorizontalSplitRegion() {
		super(Horizontal);
	}
	
	public HorizontalSplitRegion(RegionI pTopRegion, RegionI pBottomRegion) {
		super(pTopRegion, pBottomRegion, Horizontal);
	}
	
	public RegionI getTopSubRegion() {
		return mSubRegions[TopIndex];
	}
	
	public RegionI getBottomSubRegion() {
		return mSubRegions[BottomIndex];
	}
	
	public void setTopSubRegion(RegionI subRegion) {
		mSubRegions[TopIndex] = subRegion;
		subRegion.setParent(this);
		updateGeometry();
	}
	
	public void setBottomSubRegion(RegionI subRegion) {
		mSubRegions[BottomIndex] = subRegion;
		subRegion.setParent(this);
		updateGeometry();
	} 

	public void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}
	
	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		super.updateGeometry(pPosX, pPosY, pWidth, pHeight);
		
		

		if (mSubRegions[TopIndex] != null) {
			int h = mSubRegions[TopIndex].minSize().y;
			setDividerPositionNoUpdate(h/(float)pHeight);
		}
		
		
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
		
		glBegin(GL_LINES);
		glVertex2i(mPos.x,           (int)(mPos.y + mSize.y*mDividerPosition));
		glVertex2i(mPos.x + mSize.x, (int)(mPos.y + mSize.y*mDividerPosition));
		glEnd();
		glLineWidth(1.0f);
	}
}
