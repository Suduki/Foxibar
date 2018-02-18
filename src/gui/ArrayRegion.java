package gui;

public class ArrayRegion extends AbstractRegion {

	private int mNumHorizontalRegions = 1;
	private int mNumVerticalRegions = 1;
	private int mOuterOffset = 10;
	private int mInnerOffset = 10;
	private Region[][] mRegions = null;
	
	public ArrayRegion(int pNumHorizontalRegions, int pNumVerticalRegions) {
		mNumHorizontalRegions = Math.max(1, pNumHorizontalRegions);
		mNumVerticalRegions   = Math.max(1,  pNumVerticalRegions);
		mRegions = new Region[mNumHorizontalRegions][mNumVerticalRegions];
		
		// TODO: Should this really be initialized?
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				Region region = new DummyRegion();
				region.setParent(this);
				mRegions[x][y] = region;
			}
		}
		
		updateGeometry();
	}
	
	public void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}
	
	public void setRegion(int pX, int pY, Region pRegion) {
		if (pX >= 0 && pX < mNumHorizontalRegions && pY >= 0 && pY < mNumVerticalRegions) {
			Region oldRegion = mRegions[pX][pY];
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
		
		System.out.println("Updating geometry for ArrayRegion: " + pPosX + ", " + pPosY + ", " + pWidth + ", " + pHeight);
		System.out.println("  availablePixelsX = " + availablePixelsX);
		System.out.println("  availablePixelsY = " + availablePixelsY);
		System.out.println("  width = " + width);
		System.out.println("  height = " + height);
		System.out.println("  mNumHorizontalRegions = " + mNumHorizontalRegions);
		System.out.println("  mNumVerticalRegions = " + mNumVerticalRegions);
		
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				Region region = mRegions[x][y];
				if (region != null) {
					int posX = mPos.x + mOuterOffset + x*(mInnerOffset+width);
					int posY = mPos.y + mOuterOffset + y*(mInnerOffset+height);
					System.out.println("region[" + x + "][" + y + "]: " + posX + ", " + posY + ", " + width + ", " + height);
					region.updateGeometry(posX, posY, width, height);
				}
			}
		}
	}
	
	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		for (int x = 0; x < mNumHorizontalRegions; ++x) {
			for (int y = 0; y < mNumVerticalRegions; ++y) {
				Region region = mRegions[x][y];
				if (region != null) {
					region.render(pGuiRenderer);
				}
			}
		}
		return true;
	}

	public void handleMouseEventForRegion(Region pRegion, MouseEvent pEvent, MouseState pMouse) {
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
				Region region = mRegions[x][y];
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
