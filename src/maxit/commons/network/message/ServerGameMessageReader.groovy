package maxit.commons.network.message
import maxit.commons.network.message.type.IServerGameMessage;


class ServerGameMessageReader extends AbstractMessageReader<IServerGameMessage>{
	ServerGameMessageReader(){
		super('maxit.commons.network.message.serverGame')
	}
}
