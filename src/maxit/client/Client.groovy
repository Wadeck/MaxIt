package maxit.client

import groovy.transform.CompileStatic
import maxit.commons.core.IClientSendingToServer
import maxit.commons.network.message.serverGame.ChatToServerMessage
import maxit.commons.network.message.serverGame.PlayMoveMessage
import maxit.commons.network.message.serverMeta.HelloMessage
import maxit.commons.utils.SocketHelper
import maxit.display.IDisplay

import org.apache.log4j.Logger

@CompileStatic
public class Client implements IClientSendingToServer{
	private static Logger log = Logger.getLogger(Client.class)

	private Socket s

	private final int port
	private final String ip
	private ClientThread thread

	private IDisplay display

	private final String playerName

	public Client(String ip, int port, IDisplay display, String playerName) {
		this.ip = ip
		this.port = port
		this.display = display
		this.playerName = playerName
	}

	public boolean connect() {
		try {
			log.debug("Connecting...")
			this.s = SocketHelper.tryConnect(ip, port, 5)
			if(!s){
				log.fatal 'failed to connect'
				return false
			}

			log.debug("Connected")
			thread = new ClientThread(this, display)

			thread.startWithSocket(s)

			sendHello()
			log.debug("Hello sent.")

			return true
		} catch (Exception e) {
			log.error("new Socket", e)
			System.exit(-1)
		}
		return false
	}

	public void sendChat(String message) {
		thread.sendMessage(new ChatToServerMessage(message))
	}

	public void sendCoord(int x, int y) {
		thread.sendMessage(new PlayMoveMessage(x, y))
	}

	public void sendHello() {
		thread.sendMessage(new HelloMessage(playerName))
	}

	@Override
	public String getPlayerName() {
		return playerName
	}
}
