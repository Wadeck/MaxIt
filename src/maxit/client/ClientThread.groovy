package maxit.client

import groovy.transform.CompileStatic
import maxit.commons.core.IClientSendingToServer
import maxit.commons.network.message.ClientMessageReader
import maxit.commons.network.message.type.IClientGameMessage
import maxit.commons.network.message.type.IClientMessage
import maxit.commons.network.message.type.IClientMetaMessage
import maxit.commons.network.message.type.IServerMessage
import maxit.commons.network.node.GenericThread
import maxit.commons.network.node.IMessageListener
import maxit.commons.network.node.IThreadListener
import maxit.display.IDisplay

import org.apache.log4j.Logger

@CompileStatic
public class ClientThread implements IThreadListener<IClientMessage>, IMessageListener<IClientMessage> {
	private static Logger log = Logger.getLogger(ClientThread.class)
	private static ClientMessageReader reader = new ClientMessageReader()

	private GenericThread<IClientMessage> thread
	private IDisplay display
	private IClientSendingToServer client

	public ClientThread(IClientSendingToServer client, IDisplay display) {
		this.client = client
		this.display = display
		this.thread = new GenericThread<IClientMessage>(this, this, reader)
	}

	public void startWithSocket(Socket s) {
		thread.startWithSocket(s)
	}

	/**
	 * Can send either meta or game message
	 * @param message
	 */
	public void sendMessage(IServerMessage message) {
		thread.sendMessage(message)
	}

	@Override
	public void onMessage(IClientMessage m) {
		if(m instanceof IClientGameMessage){
			m.executeClientGame(display)
		}else if(m instanceof IClientMetaMessage){
			m.executeClientMeta(display)
		}
	}

	@Override
	public void removeMe(GenericThread<IClientGameMessage> thread) {
	}

	public void join(){
		thread.join()
	}
}
