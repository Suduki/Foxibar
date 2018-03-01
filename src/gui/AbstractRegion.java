package gui;

import display.Window;

public abstract class AbstractRegion implements Region {
	protected Point  mPos  = new Point();
	protected Point  mSize = new Point();
	protected Region mParentRegion = null;
	protected boolean mHasKeyboardFocus = false;
	
	@Override
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight) {
		mPos  = new Point(pPosX, pPosY);
		mSize = new Point(pWidth, pHeight);
	}

	@Override
	public Point getPos() {
		return mPos;
	}
	
	@Override
	public Point getSize() {
		return mSize;
	}
	
	@Override
	public boolean isPointInside(Point pPoint) {
		// TODO: Figure out, <,<=,>,>= ?
		return (pPoint.x > mPos.x) && (pPoint.x <= mPos.x+mSize.x) && (pPoint.y > mPos.y) && (pPoint.y <= mPos.y+mSize.y);
	}
	
	@Override
	public boolean didMouseEnter(MouseState pState) {
		return isPointInside(pState.getPos()) && !isPointInside(pState.getOldPos());
	}
	
	@Override
	public boolean didMouseLeave(MouseState pState) {
		return isPointInside(pState.getOldPos()) && !isPointInside(pState.getPos());
	}
	
	/**
	 * Forwards mouse event to region if cursor is inside. Injects ENTER or LEAVE events when needed.
	 * 
	 * @param pRegion The region to forward mouse event to.
	 * @param pState  The state of the mouse.
	 * @param pEvent  The event to be forwarded.
	 */
	public void handleMouseEventForRegion(Region pRegion, MouseEvent pEvent, MouseState pState) {
		if (pRegion != null) {
			if (pRegion.didMouseEnter(pState)) {
				pRegion.handleMouseEvent(MouseEvent.ENTER, pState);			
			}
			else if (pRegion.didMouseLeave(pState)) {
				pRegion.handleMouseEvent(MouseEvent.LEAVE, pState);
			}
			
			if (pRegion.isPointInside(pState.getPos())) {
				pRegion.handleMouseEvent(pEvent, pState);
			}
		}
	}
	
	@Override
	public Point minSize() {
		return mSize;
	}
	
	@Override
	public boolean keyboardFocusGranted() {
		mHasKeyboardFocus = true;
		return true;
	}
	
	@Override
	public void keyboardFocusRevoked() {
		mHasKeyboardFocus = false;
	}
	
	@Override
	public boolean hasKeyboardFocus() {
		return mHasKeyboardFocus;
	}
	
	@Override
	public void setParent(Region pParent) {
		mParentRegion = pParent;
	}
	
	@Override
	public Region getParent() {
		return mParentRegion;
	}
	
	@Override
	public Window getWindow() {
		if (mParentRegion != null) {
			return mParentRegion.getWindow();
		}
		
		return null;
	}
}
