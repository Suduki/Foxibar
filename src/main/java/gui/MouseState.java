package gui;

public interface MouseState {
	
	/**
	 * Retrieve the index of the last mouse button to have changed press-state.
	 * @return Index of the last changed button.
	 */
	public int getButtonIndex();
	
	/**
	 * Get the press-state for a mouse button.
	 * If the index does not refer to a valid button, this method will return false.
	 * 
	 * @param pButtonIndex Index for the button for whose state is being requested.
	 * @return The press-state of the button.
	 */
	public boolean getButtonState(int pButtonIndex);
	
	/**
	 * Get the position of the mouse cursor.
	 * @return Position in window coordinates.
	 */
	public Point getPos();
	
	/**
	 * Get the distance the mouse cursor have moved.
	 * @return Distance in window coordinates.
	 */
	public Point getDelta();
	
	/**
	 * Get the previous position of the mouse cursor. 
	 * @return Position in window coordinates.
	 */
	public Point getOldPos();
	
	/**
	 * Get the previous distance the mouse cursor have moved.
	 * @return Distance in window coordinates.
	 */
	public Point getOldDelta();
	
	/**
	 * Request keyboard focus for a region. Focus will be granted if no other region currently has focus.
	 * Only the region with keyboard focus will get keyboard events.
	 * 
	 * @param pCandidateRegion Region that will get keyboard focus.
	 * @return true if pCandidateRegion was not null and focus was granted. false otherwise.
	 */
	public boolean setKeyboardFocusCandidate(RegionI pCandidateRegion);
}
