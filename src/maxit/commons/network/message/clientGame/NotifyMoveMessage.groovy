package maxit.commons.network.message.clientGame

import groovy.transform.CompileStatic
import maxit.commons.logic.client.IClientGameLogic
import maxit.commons.network.AbstractMessage
import maxit.commons.network.message.type.IClientGameMessage

@CompileStatic
class NotifyMoveMessage extends AbstractMessage implements IClientGameMessage {
	int x, y
	int error
	int scoreH, scoreV

	NotifyMoveMessage(int x, int y, int error, int scoreH, int scoreV){
		this.x = x
		this.y = y
		this.error = error
		this.scoreH = scoreH
		this.scoreV = scoreV
	}

	NotifyMoveMessage(){
	}

	@Override
	public Map<String, String> getContent() {
		return [x: x, y: y, error: error, scoreH: scoreH, scoreV: scoreV]
	}

	@Override
	public void build(Map<String, String> map) {
		x = map['x'].toInteger()
		y = map['y'].toInteger()
		error = map['error'].toInteger()
		scoreH = map['scoreH'].toInteger()
		scoreV = map['scoreV'].toInteger()
	}

	@Override
	public void executeClientGame(IClientGameLogic logic) {
		logic.commandNotifyMove(x, y, error, scoreH, scoreV)
	}
}
