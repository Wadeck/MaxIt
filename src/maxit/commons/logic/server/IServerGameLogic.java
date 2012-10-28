package maxit.commons.logic.server;

public interface IServerGameLogic {
	void commandPlayMove(int x, int y);
	void commandClientSendChat(String content);
}
