package main.java.messages;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class MessageHandler {

	private ConcurrentLinkedQueue<Message> mIncomingMessages;
	
	public MessageHandler()
	{
		mIncomingMessages = new ConcurrentLinkedQueue<Message>();
	}
	
	public void message(Message pMessage)
	{
		mIncomingMessages.add(pMessage);	
	}
	
	public boolean handleMessages()
	{
		int numEvents = mIncomingMessages.size();
		for (int i = 0; i < numEvents; ++i) {
			Message msg = mIncomingMessages.poll();
			evaluateMessage(msg);
		}

		return true;
	}
	
	// Needs to be implemented like so in derived class:
	// pMessage.evaluate(this);
	protected abstract void evaluateMessage(Message pMessage);
}
