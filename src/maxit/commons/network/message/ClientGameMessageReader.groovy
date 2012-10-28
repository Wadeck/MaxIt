package maxit.commons.network.message

import groovy.transform.CompileStatic;
import maxit.commons.network.message.type.IClientGameMessage;

class ClientGameMessageReader extends AbstractMessageReader<IClientGameMessage>{
	ClientGameMessageReader(){
		super('maxit.commons.network.message.clientGame')
	}
}
