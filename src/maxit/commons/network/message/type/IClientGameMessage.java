package maxit.commons.network.message.type;

import maxit.commons.logic.client.IClientGameLogic;

public interface IClientGameMessage extends IClientMessage {
	void executeClientGame(IClientGameLogic logic);
}
