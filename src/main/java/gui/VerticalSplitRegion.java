package gui;

import static org.lwjgl.opengl.GL11.*;

// TODO: Figure out why 'implements Region' is needed when it is already stated further up the hierarchy. Probably some abstract base weirdness.
public class VerticalSplitRegion extends SplitRegion implements RegionI { 
	public static final int LeftIndex  = 0;
	public static final int RightIndex = 1;
	
	public VerticalSplitRegion() {
		super(Vertical);
	}
	
	public VerticalSplitRegion(RegionI pLeftRegion, RegionI pRightRegion) {
		super(pLeftRegion, pRightRegion, Vertical);
	}
	
	public RegionI getLeftSubRegion() {
		return mSubRegions[LeftIndex];
	}
	
	public RegionI getRightSubRegion() {
		return mSubRegions[RightIndex];
	}
	
	public void setLeftSubRegion(RegionI subRegion) {
		mSubRegions[LeftIndex] = subRegion;
		subRegion.setParent(this);
		updateGeometry();
	}
	
	public void setRightSubRegion(RegionI subRegion) {
		mSubRegions[RightIndex] = subRegion;
		subRegion.setParent(this);
		updateGeometry();
	} 
	
	public void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}
	
	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		super.updateGeometry(pPosX, pPosY, pWidth, pHeight);
		
		if (mSubRegions[LeftIndex] != null) {
			int w = mSubRegions[LeftIndex].minSize().x;
			setDividerPositionNoUpdate(w/(float)pWidth);
		}
		
		
		if (mSubRegions[LeftIndex] != null) {
			mSubRegions[LeftIndex].updateGeometry(mPos.x, mPos.y, (int)(mSize.x*mDividerPosition), mSize.y);
		}
		if (mSubRegions[RightIndex] != null) {
			mSubRegions[RightIndex].updateGeometry(mPos.x + (int)(mSize.x*mDividerPosition), mPos.y, (int)(mSize.x*(1.0-mDividerPosition)), mSize.y);
		}
	}
	
	@Override
	protected void renderDivider(GuiRenderer pGuiRenderer) {
		if (mMouseOverDivider) {
			glLineWidth(1 + 2*mDividerNeighbourhood);
		}
		
		glBegin(GL_LINES);
		glVertex2i((int)(mPos.x + mSize.x*mDividerPosition), mPos.y);
		glVertex2i((int)(mPos.x + mSize.x*mDividerPosition), mPos.y + mSize.y);
		glEnd();
		glLineWidth(1.0f);
	}
}
