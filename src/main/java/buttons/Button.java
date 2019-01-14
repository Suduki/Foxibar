package buttons;

import messages.Message;
import messages.MessageHandler;

import org.joml.Vector2f;

import display.Texture;

public class Button {
	private Vector2f mPosition;
	private Vector2f mSize;
	private Texture mTexture = null;
	private MessageHandler mMessageTarget;
	private Message mClickMessage;
	
	public Button(float posX, float posY)
	{
		mSize = new Vector2f(100f, 60f);
		mPosition = new Vector2f(posX, posY);
		mTexture = null;
	}
	
	public Button(Vector2f pPosition)
	{
		mSize = new Vector2f(100f, 60f);
		mPosition = pPosition;
		mTexture = null;
	}
	
	public void setTexture(Texture pTexture)
	{
		mTexture = pTexture;
	}
	
	public void setClickMessage(MessageHandler pMessageTarget, Message pClickMessage)
	{
		mMessageTarget = pMessageTarget;
		mClickMessage = pClickMessage;
	}
	
	public Texture getTexture()
	{
		return mTexture;
	}
	
	public Vector2f getPosition()
	{
		return mPosition;
	}
	
	public Vector2f getSize()
	{
		return mSize;
	}

	public boolean insideBounds(float x, float y)
	{
		return (x >= mPosition.x && x < mPosition.x + mSize.x
				&& y >= mPosition.y && y < mPosition.y + mSize.y);
	}
	
	public void click()
	{
		if ((mMessageTarget != null) && (mClickMessage != null))
		{
			mMessageTarget.message(mClickMessage);
		}
	}
}
