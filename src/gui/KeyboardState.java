package gui;

public interface KeyboardState {
	/**
	 * Get index of the latest key to change state.
	 * @return
	 */
	public int getKeyIndex();
	/**
	 * Get press-state of key referred to by the result of getKeyIndex,
	 * @return
	 */
	public boolean getKeyState();
	
	/**
	 * Get press state of arbitrary key.
	 * @param pKeyIndex
	 * @return
	 */
	public boolean getKeyState(int pKeyIndex);
}
