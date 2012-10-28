package maxit.display.none;

import groovy.transform.CompileStatic
import maxit.commons.core.IArtificialPlayer
import maxit.commons.core.IClientSendingToServer
import maxit.commons.data.Coord
import maxit.commons.data.ErrorType
import maxit.display.IDisplay

import org.apache.log4j.Logger

/**
 * Useful for IA players
 * Link between IA players and the message receiver
 */
@CompileStatic
public class NoDisplay implements IDisplay {
	private static Logger log = Logger.getLogger(NoDisplay.class);

	private final IArtificialPlayer player;
	private boolean currTurn;
	private boolean isHori;

	private Coord coord;
	private IClientSendingToServer client;

	private int myLastX;
	private int myLastY;

	public NoDisplay(IArtificialPlayer player) {
		this.player = player;
		this.coord = new Coord();
	}

	@Override
	public void setClient(IClientSendingToServer client) {
		this.client = client;
	}

	@Override
	public void commandStart(String otherName, boolean horizontal, int[] data) {
		player.initData(data, horizontal);
		this.isHori = horizontal;
		this.currTurn = true;

		if (currTurn == isHori) {
			player.getNextMove(coord);
			client.sendCoord(coord.x, coord.y);
		}
	}

	@Override
	public void commandChat(String from, String message) {
		// does not care
	}

	@Override
	public void commandNotifyMove(int x, int y, int error, int scoreH,
			int scoreV) {
		if (error == ErrorType.END) {
			if (currTurn == isHori) {
				player.notifyMyMove(x, y);
			} else {
				player.notifyOtherMove(x, y);
			}

			player.notifyEnd();
			return;
		} else if (error == ErrorType.CORRECT) {
			if (currTurn == isHori) {
				player.notifyMyMove(x, y);
			} else {
				player.notifyOtherMove(x, y);
			}
		} else {
			String err = ErrorType.getMessage(error);
			log.debug("My last move was: " + myLastX + ":" + myLastY);
			log.error("Received an other error type: " + err);
		}

		currTurn = !currTurn;
		if (currTurn == isHori) {
			player.getNextMove(coord);
			this.myLastX = coord.x;
			this.myLastY = coord.y;
			client.sendCoord(coord.x, coord.y);
		}
	}

}
