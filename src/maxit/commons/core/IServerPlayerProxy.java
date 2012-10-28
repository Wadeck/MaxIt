package maxit.commons.core;

import maxit.commons.network.message.type.IMessage;

public interface IServerPlayerProxy {
	/**
	 * @async
	 */
	public void sendMessage(IMessage message);

	/**
	 * Get the "from" name
	 */
	public String getPlayerName();

	/**
	 * Direct returns
	 */
	public boolean isStillConnected();

	public int getPlayerId();
}
