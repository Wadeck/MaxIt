package maxit.commons.network.message.type;

import maxit.commons.logic.server.IServerGameLogic;

public interface IServerGameMessage extends IServerMessage {
	void executeServerGame(IServerGameLogic logic);
}
