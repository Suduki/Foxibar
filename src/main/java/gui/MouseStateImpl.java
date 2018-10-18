package gui;

public class MouseStateImpl implements MouseState {
	private Point mPos      = new Point(-1, -1);
	private Point mOldPos   = new Point(-1, -1);
	private Point mDelta    = new Point(0, 0);
	private Point mOldDelta = new Point(0, 0);
	private int mLatestButtonIndex = -1;
	private boolean[] mButtonStates = {false, false, false, false, false, false, false, false}; // TODO: Why 8 button states, huh?
	private RegionI mKeyboardFocusCandidate = null;
		
	/**
	 * {@inheritDoc}
	 * @see #getKeyboardFocusCandidate
	 * @see #resetKeyboardFocusCandidate()
	 */
	@Override
	public boolean setKeyboardFocusCandidate(RegionI pCandidateRegion) {
		if (mKeyboardFocusCandidate != null) {
			return false;
		}
		
		mKeyboardFocusCandidate = pCandidateRegion;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * @see #setButtonState
	 */
	@Override
	public int getButtonIndex() {
		return mLatestButtonIndex;
	}

	/**
	 * {@inheritDoc}
	 * @see #setButtonState
	 */
	@Override
	public boolean getButtonState(int pButtonIndex) {
		if (pButtonIndex < 0 || pButtonIndex >= mButtonStates.length) {
			return false;
		}
		
		return mButtonStates[pButtonIndex];
	}
	
	/**
	 * {@inheritDoc}
	 * @see #setPos
	 */
	@Override
	public Point getPos() {
		return mPos;
	}

	/**
	 * {@inheritDoc}
	 * @see #setDelta
	 */
	@Override
	public Point getDelta() {
		return mDelta;
	}

	/**
	 * {@inheritDoc}
	 * @see #setPos
	 */
	@Override
	public Point getOldPos() {
		return mOldPos;
	}

	/**
	 * {@inheritDoc}
	 * @see #setDelta
	 */
	@Override
	public Point getOldDelta() {
		return mOldDelta;
	}
	
	/**
	 * Sets a new press-state for a mouse button.
	 * The index of the last modified button can be retrieved with {@link #getButtonState}.
	 * The state of any button can be retrieved through {@link #getButtonState}.
	 * 
	 * @param pButtonIndex The index of the button that will get the new press-state
	 * @param pState The new state for the button.
	 * @return true if pButtonIndex is a valid button index, false otherwise.
	 * @see #getButtonState
	 */
	public boolean setButtonState(int pButtonIndex, boolean pState) {
		if (pButtonIndex < 0 || pButtonIndex >= mButtonStates.length) {
			return false;
		}
		
		mButtonStates[pButtonIndex] = pState;
		mLatestButtonIndex = pButtonIndex;
		
		return true;
	}
	
	/**
	 * Get the region that requested keyboard focus through a successful call to {@link #setKeyboardFocusCandidate} when a mouse event was handled.
	 * After an event have been handled, {@link #resetKeyboardFocusCandidate} must be called before another region can request focus.
	 * 
	 * @return Region that requested keyboard focus. Can return null.
	 * @see #setKeyboardFocusCandidate
	 * @see #resetKeyboardFocusCandidate
	 */
	public RegionI getKeyboardFocusCandidate() {
		return mKeyboardFocusCandidate;
	}
	
	/**
	 * Reset the candidate for keyboard focus.
	 * After a call to resetKeyboardFocusCandidate, the next call to {@link #setKeyboardFocusCandidate} should be successful if called with a valid region,
	 * and {@link #getKeyboardFocusCandidate} will return null until {@link #setKeyboardFocusCandidate} have been successfully called.
	 * 
	 * @see #setKeyboardFocusCandidate
	 * @see #getKeyboardFocusCandidate
	 */
	public void resetKeyboardFocusCandidate() {
		mKeyboardFocusCandidate = null;
	}

	/**
	 * Set the new position of the mouse cursor. The position can then be retrieved by {@link #getPos()}.
	 * The old position (available through {@link #getOldPos}) will be set to the position being replaced by the new value.
	 * 
	 * @param pPos The new position of the mouse cursor in window coordinates.
	 * 
	 * @see getPos
	 * @see getOldPos
	 */
	public void setPos(Point pPos) {
		mOldPos = mPos;
		mPos = pPos;
	}
	
	/**
	 * Set the new distance moved by the mouse cursor. The distance can then be retrieved by {@link #getDelta()}.
	 * The old distance (available through {@link #getOldDelta}) will be set to the delta being replaced by the new value.
	 * 
	 * @param pDelta The new distance moved by the mouse cursor in window coordinates.
	 * 
	 * @see getDelta
	 * @see getOldDelta
	 */
	public void setDelta(Point pDelta) {
		mOldDelta = mDelta;
		mDelta = pDelta;
	}
}
