package maxit.display;

import maxit.commons.core.IClientSendingToServer;
import maxit.commons.logic.client.IClientGameLogic;
import maxit.commons.logic.client.IClientMetaLogic;

public interface IDisplay extends IClientGameLogic, IClientMetaLogic {
	void setClient(IClientSendingToServer client);
}