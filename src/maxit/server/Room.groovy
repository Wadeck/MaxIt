package maxit.server

import groovy.transform.CompileStatic
import maxit.commons.core.ILogicListener
import maxit.commons.core.IServerPlayerProxy
import maxit.commons.data.ErrorType
import maxit.commons.logic.server.ServerLogic
import maxit.commons.network.message.clientGame.NotifyMoveMessage
import maxit.commons.network.message.type.IMessage

import org.apache.log4j.Logger

@CompileStatic
public class Room implements ILogicListener {
	private static Logger log = Logger.getLogger(Room.class)

	private IServerPlayerProxy p1
	private IServerPlayerProxy p2
	private ServerLogic logic
	private final Server server

	public Room(Server server) {
		this.server = server
		this.reset()
	}

	public void reset() {
		this.logic = new ServerLogic(this)
		this.p1 = null
		this.p2 = null
	}

	public boolean isFull() {
		if (p1 != null && p2 != null && p1.isStillConnected()
		&& p2.isStillConnected()) {
			return true
		} else {
			return false
		}
	}

	@Override
	public void notifyStart() {
		server.notifyStart(this)
	}

	@Override
	public void notifyEnd(int scoreH, int scoreV) {
		log.info("Score: ${ p1.getPlayerName() }:${ scoreH } vs ${ p2.getPlayerName() }:${ scoreV }")
		server.notifyEnd(this)
	}

	public void addPlayer(IServerPlayerProxy player) {
		if (p1 == null || !p1.isStillConnected()) {
			if (p2 == null) {
				// normal case
				this.p1 = player
			} else {
				if (p2.isStillConnected()) {
					// there is a second player, so we put it as first
					this.p1 = p2
					this.p2 = player
				} else {
					// the second players leaves
					this.p2 = null
					this.p1 = player
				}
			}
		} else {
			// the first player is already there
			this.p2 = player
		}

		if (isFull()) {
			// start
			logic.setPlayers(p1, p2)
		}
	}

	public void removeMe(IServerPlayerProxy player) {
		if (!logic.isStarted() || logic.isEnded()) {
			return
		}
		IMessage message = null
		if (player == p1) {
			message = new NotifyMoveMessage(0, 0, ErrorType.END, Integer.MIN_VALUE,
					logic.getScoreH())
			p2.sendMessage(message)
		} else if (player == p2) {
			message = new NotifyMoveMessage(0, 0, ErrorType.END, logic.getScoreV(),
					Integer.MIN_VALUE)
			p1.sendMessage(message)
		}
		logic.notifyPlayerLeft(player)
		server.notifyEnd(this)
	}

	public ServerLogic getLogic(){
		return logic
	}

	@Override
	public String toString() {
		return "Room[" + (p1?.getPlayerName() ?: '') + ", " + (p2?.getPlayerName() ?: '') + "]"
	}

	public IServerPlayerProxy getP1(){
		return p1
	}

	public IServerPlayerProxy getP2(){
		return p2
	}
}
