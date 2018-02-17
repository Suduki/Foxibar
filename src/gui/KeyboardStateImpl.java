package gui;

public class KeyboardStateImpl implements KeyboardState {
	public static final int NumKeyStates = 512;
	boolean[] mKeyStates;
	private int mLastKeyIndex = 0;
	
	KeyboardStateImpl() {
		mKeyStates = new boolean[NumKeyStates];
	}
	
	@Override
	public int getKeyIndex() {
		return mLastKeyIndex;
	}

	@Override
	public boolean getKeyState(int pKeyIndex) {
		if (pKeyIndex >= 0 && pKeyIndex < NumKeyStates) {
			return mKeyStates[pKeyIndex];
		}
		
		return false;
	}
	
	public void setKeyState(int pKeyIndex, boolean pState) {
		mLastKeyIndex = pKeyIndex;
		if (pKeyIndex >= 0 && pKeyIndex < NumKeyStates) {
			mKeyStates[pKeyIndex] = pState;
		}
	}

	@Override
	public boolean getKeyState() {
		return mKeyStates[mLastKeyIndex];
	}
}
