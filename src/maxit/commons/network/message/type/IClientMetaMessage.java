package maxit.commons.network.message.type;

import maxit.commons.logic.client.IClientMetaLogic;

public interface IClientMetaMessage extends IClientMessage {
	void executeClientMeta(IClientMetaLogic logic);

}
