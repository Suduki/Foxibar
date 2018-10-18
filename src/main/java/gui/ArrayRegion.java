package gui;

import java.util.ArrayList;

public class ArrayRegion extends AbstractRegion {

	public static final int Horizontal = 0;
	public static final int Vertical = 1;
	
	private ArrayList<RegionI> mSubRegions;
	private Point mSpacing = new Point(8,8);
	private int mDirection;
	
	public ArrayRegion(int pDirection) {
		mSubRegions = new ArrayList<RegionI>();
		mDirection = pDirection;
	}
	
	public void insertRegion(int index, RegionI region) {
		region.setParent(this);
		mSubRegions.add(index, region);
		updateGeometry();
	}
	
	public Point getSpacing() {
		return new Point(mSpacing);
	}
	
	public void setSpacing(int pSpacing) {
		mSpacing.x = mSpacing.y = Math.max(0,  pSpacing);
	}
	
	public void setSpacing(int pSpacingX, int pSpacingY) {
		mSpacing.x = Math.max(0, pSpacingX);
		mSpacing.y = Math.max(0, pSpacingY);
	}
	
	private void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}
	
	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		super.updateGeometry(pPosX, pPosY, pWidth, pHeight);
		
		if (mSubRegions.size() > 0) {
			
			if (mDirection == Horizontal) {
				int x = mPos.x + mSpacing.x;
				for (RegionI r : mSubRegions) {
					int w = r.minSize().x;
					r.updateGeometry(x, mPos.y + mSpacing.y, w, mSize.y - mSpacing.y*2);
					x += (w + mSpacing.x);
				}
			}
			else {
				int y = mPos.y + mSpacing.y;
				for (RegionI r : mSubRegions) {
					int h = r.minSize().y;
					r.updateGeometry(mPos.x + mSpacing.x, y, mSize.x - mSpacing.x*2, h);
					y += (h + mSpacing.y);
				}
			}
		}
	}
	
	@Override
	public Point minSize() {
		Point min = new Point(0,0);
		if (mDirection == Horizontal) {
			for (RegionI region : mSubRegions) {
				Point size = region.minSize();
				min.x += size.x + mSpacing.x;
				min.y = Math.max(min.y, size.y);
			}
			
			min.x += mSpacing.x;
			min.y += mSpacing.y * 2;
		}
		else {
			for (RegionI region : mSubRegions) {
				Point size = region.minSize();
				min.x = Math.max(min.x, size.x);
				min.y = size.y + mSpacing.y;
			}
			
			min.x += mSpacing.x * 2;
			min.y += mSpacing.y;
		}
		
		return min;
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {		
		for (RegionI r : mSubRegions) {
			r.render(pGuiRenderer);
		}
		return false;
	}

	private boolean handleDividerDragging(MouseEvent pEvent, MouseState pMouse) { return false; }
	
	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (!handleDividerDragging(pEvent, pMouse)) {
			for (RegionI r : mSubRegions) {
				handleMouseEventForRegion(r, pEvent, pMouse);
			}
		}
		return false;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pState) {
		// TODO Auto-generated method stub
		return false;
	}

}
