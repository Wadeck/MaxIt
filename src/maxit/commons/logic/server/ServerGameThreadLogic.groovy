package maxit.commons.logic.server

import groovy.transform.CompileStatic;

@CompileStatic
class ServerGameThreadLogic implements IServerGameLogic {
	private ServerLogic logic
	private int playerId

	ServerGameThreadLogic(ServerLogic logic, int playerId){
		this.logic = logic
		this.playerId = playerId
	}

	@Override
	public void commandPlayMove(int x, int y) {
		logic.commandPlayMove(playerId, x, y)
	}

	@Override
	public void commandClientSendChat(String content) {
		logic.commandServerChat(playerId, content)
	}
}
