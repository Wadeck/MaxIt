package maxit.commons.network.message.clientMeta

import groovy.transform.CompileStatic
import maxit.commons.logic.client.IClientMetaLogic
import maxit.commons.network.AbstractMessage
import maxit.commons.network.message.type.IClientMetaMessage
import maxit.commons.utils.ArrayHelper

@CompileStatic
class StartMessage extends AbstractMessage implements IClientMetaMessage {
	String otherName
	boolean horizontal
	int[] data

	StartMessage(String otherName, boolean horizontal, int[] data){
		this.otherName = otherName
		this.horizontal = horizontal
		this.data = data
	}
	
	StartMessage(){
	}

	@Override
	public Map<String, String> getContent() {
		return [
			otherName: otherName,
			horizontal: horizontal ? '1' :'0',
			data: ArrayHelper.join(data, ':')
		]
	}

	@Override
	public void build(Map<String, String> map) {
		otherName = map['otherName']
		horizontal = map['horizontal'] == '1' ? true : false
		data = map['data']?.split(':').collect{ String elem -> Integer.parseInt(elem) } as int[]
	}

	@Override
	public void executeClientMeta(IClientMetaLogic logic) {
		logic.commandStart(otherName, horizontal, data)
	}
}
