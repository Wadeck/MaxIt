package maxit.commons.network.message

import maxit.commons.network.message.type.IClientMetaMessage;


class ClientMetaMessageReader extends AbstractMessageReader<IClientMetaMessage>{
	ClientMetaMessageReader(){
		super('maxit.commons.network.message.clientMeta')
	}
}
