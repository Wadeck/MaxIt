package maxit

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked;
import maxit.client.Client
import maxit.commons.core.IArtificialPlayer
import maxit.display.IDisplay
import maxit.display.none.NoDisplay

import org.apache.log4j.Logger

public class MaxitIA {
	private static Logger log = Logger.getLogger(NoDisplay.class)
	private Client client
	private IDisplay display
	private IArtificialPlayer player

	public MaxitIA(String ip, int port, String playerName, iaArgs = []) {
		Class<IArtificialPlayer> clazz
		try {
			clazz = (Class<IArtificialPlayer>) Thread.currentThread()
					.getContextClassLoader()
					.loadClass("maxit.ia.impl." + playerName + "Player")
			// spread the array into arguments
			this.player = clazz.newInstance(*iaArgs)
		} catch (Exception e) {
			log.error("Error when loading the ia player", e)
		}
		this.display = new NoDisplay(player)

		this.client = new Client(ip, port, display, playerName + "-"
				+ (int) (Math.random() * 1000))

		this.display.setClient(client)

		if(client.connect()){
			try {
				client.thread.join()
			} catch (InterruptedException e) {
				e.printStackTrace()
			}
		}
	}

	public static void main(String[] args) {
		def iaArgs = args.length > 3 ? args[3..args.length-1] : []
		new MaxitIA(args[0], Integer.parseInt(args[1]), args[2], iaArgs)
	}
}
