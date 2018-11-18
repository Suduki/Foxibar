package gui;

public class GridRegion extends AbstractRegion {

	private int mNumHorizontalRegions = 1;
	private int mNumVerticalRegions = 1;
	private int mOuterOffset = 10;
	private int mInnerOffset = 10;
	private RegionI[][] mRegions = null;
	
	public GridRegion(int pNumHorizontalRegions, int pNumVerticalRegions) {
		mNumHorizontalRegions = Math.max(1, pNumHorizontalRegions);
		mNumVerticalRegions   = Math.max(1,  pNumVerticalRegions);
		mRegions = new RegionI[mNumHorizontalRegions][mNumVerticalRegions];
		
		// TODO: Should this really be initialized?
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				RegionI region = new DummyRegion();
				region.setParent(this);
				mRegions[x][y] = region;
			}
		}
		
		updateGeometry();
	}
	
	public void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}
	
	public void setRegion(int pX, int pY, RegionI pRegion) {
		if (pX >= 0 && pX < mNumHorizontalRegions && pY >= 0 && pY < mNumVerticalRegions) {
			RegionI oldRegion = mRegions[pX][pY];
			mRegions[pX][pY] = pRegion;
			
			if (pRegion != null) {
				pRegion.setParent(this);
			}
			
			if (oldRegion != null) {
				oldRegion.setParent(null);
			}
		}
		
		updateGeometry();
	}
	
	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		super.updateGeometry(pPosX, pPosY, pWidth, pHeight);
		int availablePixelsX = Math.max(mNumHorizontalRegions, mSize.x - 2*mOuterOffset - (mNumHorizontalRegions-1)*mInnerOffset);
		int availablePixelsY = Math.max(mNumVerticalRegions, mSize.y - 2*mOuterOffset - (mNumVerticalRegions-1)*mInnerOffset);
		int width  = availablePixelsX/mNumHorizontalRegions;
		int height = availablePixelsY/mNumVerticalRegions;
		
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				RegionI region = mRegions[x][y];
				if (region != null) {
					int posX = mPos.x + mOuterOffset + x*(mInnerOffset+width);
					int posY = mPos.y + mOuterOffset + y*(mInnerOffset+height);
					region.updateGeometry(posX, posY, width, height);
				}
			}
		}
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				RegionI region = mRegions[x][y];
				if (region != null) {
					region.render(pGuiRenderer);
				}
			}
		}
		return true;
	}
	
	Point getRegionIndex(RegionI pRegion) {
		if (pRegion != null) {
			for (int x = 0; x < mNumHorizontalRegions; ++x) {
				for (int y = 0; y < mNumVerticalRegions; ++y) {
					if (mRegions[x][y] == pRegion) {
						return new Point(x,y);
					}
				}
			}
		}
		
		return null;
	}

	public void handleMouseEventForRegion(RegionI pRegion, MouseEvent pEvent, MouseState pMouse) {
		if (pRegion.didMouseEnter(pMouse)) {
			pRegion.handleMouseEvent(MouseEvent.ENTER, pMouse);			
		}
		else if (pRegion.didMouseLeave(pMouse)) {
			pRegion.handleMouseEvent(MouseEvent.LEAVE, pMouse);
		}
		
		if (pRegion.isPointInside(pMouse.getPos())) {
			pRegion.handleMouseEvent(pEvent, pMouse);
		}
	}
	
	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				RegionI region = mRegions[x][y];
				if (region != null) {
					handleMouseEventForRegion(region, pEvent, pMouse);
				}
			}
		}
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pState) {
		// TODO Auto-generated method stub
		return false;
	}

}
