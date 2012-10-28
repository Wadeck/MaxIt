package maxit.commons.network.message

import maxit.commons.network.message.type.IMessage
import maxit.commons.network.message.type.IServerMetaMessage;

class ServerMetaMessageReader extends AbstractMessageReader<IServerMetaMessage>{
	ServerMetaMessageReader(){
		super('maxit.commons.network.message.serverMeta')
	}
}
