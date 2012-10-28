package maxit.commons.network.message.serverGame

import groovy.transform.CompileStatic
import maxit.commons.logic.server.IServerGameLogic
import maxit.commons.network.AbstractMessage
import maxit.commons.network.message.type.IServerGameMessage

@CompileStatic
class PlayMoveMessage extends AbstractMessage implements IServerGameMessage {
	int x, y

	PlayMoveMessage(int x, int y) {
		this.x = x
		this.y = y
	}

	PlayMoveMessage() {
	}

	@Override
	public Map<String, String> getContent() {
		return [x: x, y: y]
	}

	@Override
	public void build(Map<String, String> map) {
		x = map['x'].toInteger()
		y = map['y'].toInteger()
	}

	@Override
	public void executeServerGame(IServerGameLogic logic) {
		logic.commandPlayMove(x, y)
	}
}
