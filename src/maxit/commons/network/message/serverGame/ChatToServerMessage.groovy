package maxit.commons.network.message.serverGame

import groovy.transform.CompileStatic;
import maxit.commons.logic.server.IServerGameLogic
import maxit.commons.network.AbstractMessage
import maxit.commons.network.message.type.IServerGameMessage

@CompileStatic
class ChatToServerMessage extends AbstractMessage implements IServerGameMessage {
	String content

	ChatToServerMessage(String content){
		this.content = content
	}
	
	ChatToServerMessage(){
	}
	
	@Override
	public Map<String, String> getContent() {
		return [content: content]
	}

	@Override
	public void build(Map<String, String> map) {
		content = map['content']
	}

	@Override
	public void executeServerGame(IServerGameLogic logic) {
		logic.commandClientSendChat(content)
	}
}
