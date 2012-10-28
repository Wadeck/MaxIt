package maxit.commons.logic.client;

public interface IClientGameLogic {
	void commandChat(String fromName, String content);
	
	void commandNotifyMove(int x, int y, int error, int scoreH,
			int scoreV);
}
