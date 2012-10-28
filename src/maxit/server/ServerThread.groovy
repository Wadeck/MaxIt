package maxit.server

import groovy.transform.CompileStatic
import maxit.commons.core.IServerPlayerProxy
import maxit.commons.logic.server.IServerGameLogic
import maxit.commons.network.message.ServerGameMessageReader
import maxit.commons.network.message.type.IMessage
import maxit.commons.network.message.type.IServerGameMessage
import maxit.commons.network.node.GenericThread
import maxit.commons.network.node.IMessageListener
import maxit.commons.network.node.IThreadListener

import org.apache.log4j.Logger

@CompileStatic
public class ServerThread implements IServerPlayerProxy, IMessageListener<IServerGameMessage>, IThreadListener<IServerGameMessage>, IServerGameLogic {
	private static final Logger log = Logger.getLogger(ServerThread.class)
	private static final ServerGameMessageReader reader = new ServerGameMessageReader()

	private String playerName

	private GenericThread<IServerGameMessage> thread

	private boolean occupied

	private final Room room
	
	private final int id

	public ServerThread(Room room, String playerName, int id) {
		this.room = room
		this.playerName = playerName
		this.id = id
		this.thread = new GenericThread<IServerGameMessage>(this, this, reader)
	}

	/**
	 * To launch the thread
	 * @param s Socket, opened
	 */
	public void startWithSocket(Socket s,
	BufferedReader buffInput, BufferedWriter buffOutput) {
		thread.startWithSocket(s, buffInput, buffOutput)
		this.occupied = true
	}

	@Override
	public void sendMessage(IMessage message) {
		thread.sendMessage(message)
	}

	@Override
	public final String getPlayerName() {
		return playerName
	}
	
	@Override
	public int getPlayerId() {
		return id
	}
	
	@Override
	public boolean isStillConnected() {
		return thread.isStillConnected()
	}

	@Override
	public void removeMe(GenericThread<IServerGameMessage> thread) {
		room.removeMe(this)
	}

	@Override
	public void onMessage(IServerGameMessage message) {
		message.executeServerGame(this)
	}

	@Override
	public void commandPlayMove(int x, int y) {
		room.logic.commandPlayMove(id, x, y)
	}

	@Override
	public void commandClientSendChat(String content) {
		room.logic.commandServerChat(id, content)
	}
}
