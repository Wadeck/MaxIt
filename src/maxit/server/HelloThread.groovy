package maxit.server

import groovy.transform.CompileStatic
import maxit.commons.logic.server.IServerMetaLogic
import maxit.commons.network.message.ServerMetaMessageReader
import maxit.commons.network.message.type.IServerMetaMessage
import maxit.commons.network.node.GenericThread
import maxit.commons.network.node.IMessageListener
import maxit.commons.network.node.IThreadListener

import org.apache.log4j.Logger

@CompileStatic
public class HelloThread implements IThreadListener<IServerMetaMessage>, IMessageListener<IServerMetaMessage>, IServerMetaLogic {
	private static final Logger log = Logger.getLogger(HelloThread.class)
	private static final ServerMetaMessageReader reader = new ServerMetaMessageReader()

	private final Server server
	private String playerName
	private GenericThread<IServerMetaMessage> thread

	private boolean occupied

	public HelloThread(Server server) {
		this.server = server
		this.thread = new GenericThread<IServerMetaMessage>(this, this, reader)
		this.occupied = false
	}

	public boolean isAlreadyOccupied() {
		return occupied
	}

	/**
	 * To launch the thread
	 * @param s Socket, opened
	 */
	public void startWithSocket(Socket s) {
		thread.startWithSocket(s)
		this.occupied = true
	}

	private void sendPlayerBackToServer() {
		log.debug("hello recu, on donne la main a un vrai server thread")
		this.server.addNewPlayer(playerName, thread.getSocket(),
				thread.getInputBuffer(), thread.getOutputBuffer())

		this.occupied = false
		this.playerName = null
		thread.reset()
	}

	public void sendMessage(IServerMetaMessage message) {
		thread.sendMessage(message)
	}

	@Override
	public void onMessage(IServerMetaMessage message) {
		message.executeServerMeta(this)
		if (playerName != null) {
			sendPlayerBackToServer()
		}
	}

	public void commandHello(String myName){
		this.playerName = myName
	}

	@Override
	public void removeMe(GenericThread<IServerMetaMessage> thread) {
		server.removeMe(this)
	}

	@Override
	public void setPlayerName(String name) {
		this.playerName = name
	}
}
