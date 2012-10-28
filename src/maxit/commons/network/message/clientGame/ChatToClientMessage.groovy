package maxit.commons.network.message.clientGame

import groovy.transform.CompileStatic
import maxit.commons.logic.client.IClientGameLogic
import maxit.commons.network.AbstractMessage
import maxit.commons.network.message.type.IClientGameMessage

@CompileStatic
class ChatToClientMessage extends AbstractMessage implements IClientGameMessage {
	String content
	String fromName

	ChatToClientMessage(String content, String fromName) {
		this.content = content
		this.fromName = fromName
	}
	
	ChatToClientMessage() {
	}

	@Override
	public Map<String, String> getContent() {
		return [content: content, fromName: fromName]
	}

	@Override
	public void build(Map<String, String> map) {
		content = map['content']
		fromName = map['fromName']
	}

	@Override
	public void executeClientGame(IClientGameLogic logic) {
		logic.commandChat(fromName, content)
	}
}
