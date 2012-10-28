package maxit.commons.core;

public interface IClientSendingToServer {
	public String getPlayerName();

	public void sendChat(String message);

	public void sendCoord(int x, int y);

	public void sendHello();

}