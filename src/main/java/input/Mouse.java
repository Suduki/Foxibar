package main.java.input;

import main.java.math.Vector2f;

public class Mouse {
	
	public static final int NUM_BUTTONS = 3;
	
	boolean m_buttonPressed[];
	float m_posX, m_posY;
	
	public Mouse()
	{
		m_posX = m_posY = -1;
		
		m_buttonPressed = new boolean[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; ++i)
		{
			m_buttonPressed[i] = false;
		}
	}
	
	public void dump()
	{
		System.out.print("Mouse: x=" + m_posX + ", y=" + m_posY);
		for (int i = 0; i < NUM_BUTTONS; ++i)
		{
			System.out.print(", b[" + i + "]=" + m_buttonPressed[i]);
		}
		System.out.println();
	}
	
	public Mouse(Mouse other)
	{
		m_posX = other.m_posX;
		m_posY = other.m_posY;
		m_buttonPressed = new boolean[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; ++i)
		{
			m_buttonPressed[i] = other.m_buttonPressed[i];
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
			m_buttonPressed[button] = pressed;
		}
	}
	
	public boolean buttonPressed(int button)
	{
		if (button >= 0 && button < NUM_BUTTONS)
		{
			return m_buttonPressed[button];
		}
		
		return false;
	}
	
	public void setPosition(float x, float y)
	{
		m_posX = x;
		m_posY = y;
	}
	
	public Vector2f getPos()
	{
		return new Vector2f(m_posX, m_posY);
	}
	
	public float getX()
	{
		return m_posX;
	}
	
	public float getY()
	{
		return m_posY;
	}
}
