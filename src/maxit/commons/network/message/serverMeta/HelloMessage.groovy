package maxit.commons.network.message.serverMeta

import maxit.commons.logic.server.IServerMetaLogic
import maxit.commons.network.AbstractMessage
import maxit.commons.network.message.type.IServerMetaMessage

class HelloMessage extends AbstractMessage implements IServerMetaMessage {
	String myName

	HelloMessage(String myName) {
		this.myName = myName
	}

	HelloMessage() {
	}

	@Override
	public Map<String, String> getContent() {
		return [myName: myName]
	}

	@Override
	public void build(Map<String, String> map) {
		myName = map['myName']
	}

	@Override
	public void executeServerMeta(IServerMetaLogic logic) {
		logic.commandHello(myName)
	}
}
