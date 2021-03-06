package gui;

import static org.lwjgl.opengl.GL11.*;

public abstract class SplitRegion extends AbstractRegion {
	public static final int mDividerNeighbourhood = 4;
	public static final int Vertical   = 0;
	public static final int Horizontal = 1;
	
	protected RegionI mSubRegions[] = {null, null};
	protected double mDividerPosition = 0.5;
	protected boolean mMovingDivider = false;
	protected int mMovingDividerPosition = -1;
	protected boolean mMouseOverDivider = false;
	protected int mDirection;
	
	protected abstract void renderDivider(GuiRenderer pGuiRenderer);
	
	public SplitRegion(int pDirection) {
		super();
		mDirection = pDirection;
	}
	
	public SplitRegion(RegionI pSubRegion0, RegionI pSubRegion1, int pDirection) {
		this(pDirection);
		mSubRegions[0] = pSubRegion0;
		mSubRegions[1] = pSubRegion1;
		
		if (mSubRegions[0] != null) {
			mSubRegions[0].setParent(this);
		}
		if (mSubRegions[1] != null) {
			mSubRegions[1].setParent(this);
		}
		
		updateGeometry();
	}

	public void updateGeometry() {
		updateGeometry(mPos.x, mPos.y, mSize.x, mSize.y);
	}

	public void setDividerPosition(double pNormalizedPosition) {
		System.out.println("SplitRegion.setDividerPosition(" + pNormalizedPosition + ");");
		mDividerPosition = Math.max(0.0, Math.min(1.0, pNormalizedPosition));
		updateGeometry();
	}
	
	protected void setDividerPositionNoUpdate(double pNormalizedPosition) {
		System.out.println("SplitRegion.setDividerPosition(" + pNormalizedPosition + ");");
		mDividerPosition = Math.max(0.0, Math.min(1.0, pNormalizedPosition));
	}

	@Override
	public boolean render(GuiRenderer pGuiRenderer) {
		if (mSubRegions[0] != null) {
			mSubRegions[0].render(pGuiRenderer);
		}
		if (mSubRegions[1] != null) {
			mSubRegions[1].render(pGuiRenderer);
		}
		
		glColor3f(1,1,1);
		renderDivider(pGuiRenderer);
		
		return true;
	}
	
	private boolean setMouseOverDivider(MouseState pMouse) {
		int mousePos = (mDirection == Vertical) ? pMouse.getPos().x : pMouse.getPos().y;
		int pos      = (mDirection == Vertical) ? mPos.x : mPos.y;
		int size     = (mDirection == Vertical) ? mSize.x : mSize.y;
		int dividerPos = pos + (int)(mDividerPosition*size);
		return mMouseOverDivider = (mousePos >= dividerPos-mDividerNeighbourhood && mousePos < dividerPos+mDividerNeighbourhood) || mMovingDivider;
	}
	
	private boolean handleDividerDragging(MouseEvent pEvent, MouseState pMouse) {
		boolean dividerHandled = mMovingDivider;
		setMouseOverDivider(pMouse);
		if (pEvent == MouseEvent.BUTTON) {
			int index = pMouse.getButtonIndex();
			boolean pressed = pMouse.getButtonState(index);
			
			if (pressed) {
				if (mMouseOverDivider) {
					mMovingDivider = true;
					int size     = (mDirection == Vertical) ? mSize.x : mSize.y;
					int dividerPos = (int)(mDividerPosition*size);
					mMovingDividerPosition = dividerPos;
					dividerHandled = true;
				}
				
			}
			else if (mMovingDivider){
				mMovingDivider = false;
				mMovingDividerPosition = -1;
			}
		}
		
		if (pEvent == MouseEvent.ENTER && !pMouse.getButtonState(0))
		{
			mMovingDivider = false;
			dividerHandled = false;
		}
		
		if (mMovingDivider && pEvent == MouseEvent.MOTION) {
			int mousePos = (mDirection == Vertical) ? pMouse.getPos().x : pMouse.getPos().y;
			int pos      = (mDirection == Vertical) ? mPos.x : mPos.y;
			float size   = (mDirection == Vertical) ? mSize.x : mSize.y;
			float normalizeSize = (size > 0) ? 1.0f/size : 0.0f;
			mMovingDividerPosition = mousePos - pos;
			setDividerPosition(mMovingDividerPosition * normalizeSize);
		}
		
		return dividerHandled;
	}
	
	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (!handleDividerDragging(pEvent, pMouse)) {
			handleMouseEventForRegion(mSubRegions[0], pEvent, pMouse);
			handleMouseEventForRegion(mSubRegions[1], pEvent, pMouse);
		}
		
		return true;
	}
	
	@Override
	public boolean handleKeyboardEvent(KeyboardState pEvent) {
		System.out.println("SplitRegion Keyboard Event");
		return false;
	}
}
