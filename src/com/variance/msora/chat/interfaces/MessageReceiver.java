package com.variance.msora.chat.interfaces;

public interface MessageReceiver {

	public void messageReceived(String message);

	void socketClosed();
}
