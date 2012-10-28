package maxit

import groovy.transform.CompileStatic
import maxit.client.Client
import maxit.display.IDisplay
import maxit.display.gui.GuiDisplay

@CompileStatic
public class MaxitClient {
	private Client client
	private IDisplay display

	public MaxitClient(String ip, int port, String playerName) {
		this.display = new GuiDisplay()
		this.client = new Client(ip, port, display, playerName + "-"
				+ (int) (Math.random() * 1000))
		this.display.setClient(client)
		
		if(!client.connect()){
			System.exit(-1)
		}
	}

	public static void main(String[] args) {
		new MaxitClient(args[0], Integer.parseInt(args[1]), args[2])
	}
}
