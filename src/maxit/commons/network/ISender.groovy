package maxit.commons.network

import maxit.commons.network.message.type.IMessage;

public interface ISender {
	void sendMessage(IMessage message)
}