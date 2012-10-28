package maxit.commons.network.message.type;

import maxit.commons.logic.server.IServerMetaLogic;

public interface IServerMetaMessage extends IServerMessage {
	void executeServerMeta(IServerMetaLogic logic);
}
