package input;

import math.Vector2f;

public class Mouse {
	
	public static final int NUM_BUTTONS = 3;
	
	boolean mButtonPressed[];
	float mPosX, mPosY;
	float mDeltaX, mDeltaY;
	
	public Mouse()
	{
		mPosX = mPosY = -1;
		
		mButtonPressed = new boolean[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; ++i)
		{
			mButtonPressed[i] = false;
		}
	}
	
	public void dump()
	{
		System.out.print("Mouse: x=" + mPosX + ", y=" + mPosY);
		for (int i = 0; i < NUM_BUTTONS; ++i)
		{
			System.out.print(", b[" + i + "]=" + mButtonPressed[i]);
		}
		System.out.println();
	}
	
	public Mouse(Mouse other)
	{
		mPosX = other.mPosX;
		mPosY = other.mPosY;
		mButtonPressed = new boolean[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; ++i)
		{
			mButtonPressed[i] = other.mButtonPressed[i];
		}
	}
	
	public boolean validButtonIndex(int buttonIndex)
	{
		return (buttonIndex >= 0) && (buttonIndex < NUM_BUTTONS);
	}
	
	public void setButtonPressed(int button, boolean pressed)
	{
		if (validButtonIndex(button))
		{
			mButtonPressed[button] = pressed;
		}
	}
	
	public boolean buttonPressed(int button)
	{
		if (button >= 0 && button < NUM_BUTTONS)
		{
			return mButtonPressed[button];
		}
		
		return false;
	}
	
	public void setPosition(float x, float y)
	{
		mDeltaX = x - mPosX;
		mDeltaY = y - mPosY;
		mPosX = x;
		mPosY = y;
	}
	
	
	public Vector2f getPos()
	{
		return new Vector2f(mPosX, mPosY);
	}
	
	public float getX()
	{
		return mPosX;
	}
	
	public float getY()
	{
		return mPosY;
	}
	
	public float getDeltaX() {
		return mDeltaX;
	}
	
	public float getDeltaY() {
		return mDeltaY;
	}
}
