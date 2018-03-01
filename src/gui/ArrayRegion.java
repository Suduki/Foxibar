package gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import com.sun.javafx.geom.transform.GeneralTransform3D;

public class ArrayRegion extends AbstractRegion {

	public static final int Horizontal = 0;
	public static final int Vertical = 1;
	
	private ArrayList<Region> mSubRegions;
	private int mSpacing = 8;
	private int mDirection;
	
	public ArrayRegion(int pDirection) {
		mSubRegions = new ArrayList<Region>();
		mDirection = pDirection;
	}
	
	public void insertRegion(int index, Region region) {
		region.setParent(this);
		mSubRegions.add(index, region);
		updateGeometry();
	}
	
	public int getSpacing() {
		return mSpacing;
	}
	
	public void setSpacing(int pSpacing) {
		mSpacing = Math.max(0,  pSpacing);
	}
	
	private void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}
	
	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		// TODO Auto-generated method stub
		super.updateGeometry(pPosX, pPosY, pWidth, pHeight);
		
		if (mSubRegions.size() > 0) {
			
			if (mDirection == Horizontal) {
				int x = mPos.x + mSpacing;
				for (Region r : mSubRegions) {
					int w = r.minSize().x;
					r.updateGeometry(x, mSpacing/2, w, mSize.y - mSpacing);
					x += (w + mSpacing);
				}
			}
			else {
				int y = mPos.y + mSpacing;
				for (Region r : mSubRegions) {
					int h = r.minSize().y;
					r.updateGeometry(mSpacing/2, y, mSize.x-mSpacing, h);
					y += (h + mSpacing);
				}
			}
		}
	}
	
	@Override
	public Point minSize() {
		Point min = new Point(0,0);
		for (Region r : mSubRegions) {
			Point size = r.minSize();
			min.x = Math.max(min.x, size.x);
			min.y = Math.max(min.y, size.y);
		}
		min.x += mSpacing;
		min.y += mSpacing;
		return min;
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {		
		for (Region r : mSubRegions) {
			r.render(pGuiRenderer);
		}
		return false;
	}

	private boolean handleDividerDragging(MouseEvent pEvent, MouseState pMouse) { return false; }
	
	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (!handleDividerDragging(pEvent, pMouse)) {
			for (Region r : mSubRegions) {
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
