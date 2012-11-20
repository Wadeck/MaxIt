package maxit.commons.logic.server

import groovy.transform.CompileStatic;

import java.util.ArrayList
import java.util.List

import maxit.commons.core.ILogicListener
import maxit.commons.core.IServerPlayerProxy
import maxit.commons.data.CellConstant
import maxit.commons.data.DataFactory
import maxit.commons.data.ErrorType
import maxit.commons.data.ServerData
import maxit.commons.network.message.clientGame.ChatToClientMessage
import maxit.commons.network.message.clientGame.NotifyMoveMessage
import maxit.commons.network.message.clientMeta.StartMessage
import maxit.commons.network.message.type.IMessage

import org.apache.log4j.Logger

@CompileStatic
public class ServerLogic {
	private static Logger log = Logger.getLogger(ServerLogic.class)

	private int[] initialData
	private ServerData data
	private int current
	private boolean horizontal

	private int scoreH, scoreV

	private IServerPlayerProxy hPlayer
	private IServerPlayerProxy vPlayer
	private ILogicListener listener

	private boolean started
	private boolean ended

	/**
	 * Contains the list of move done by each player
	 */
	private List<Integer> moves

	/**
	 * Automatically starts
	 * 
	 * @param hPlayer
	 * @param vPlayer
	 */
	public ServerLogic(ILogicListener listener, IServerPlayerProxy hPlayer,
	IServerPlayerProxy vPlayer) {
		this.init(listener)

		this.hPlayer = hPlayer
		this.vPlayer = vPlayer

		this.start()
	}

	/**
	 * Need to be started manually when two players are added
	 * 
	 * @param hPlayer
	 * @param vPlayer
	 */
	public ServerLogic(ILogicListener listener) {
		this.init(listener)
	}

	private void init(ILogicListener listener) {
		this.started = false
		this.ended = false

		this.listener = listener
//		this.data = DataFactory.create()
		this.data = DataFactory.create(0)//TODO random put here
		this.horizontal = true
		this.current = data.getStartY()
		this.scoreH = scoreV = 0
		this.moves = new ArrayList<Integer>()
	}

	public boolean isStarted() {
		return started
	}

	public boolean isEnded() {
		return ended
	}

	public void setPlayers(IServerPlayerProxy p1, IServerPlayerProxy p2) {
		this.hPlayer = p1
		this.vPlayer = p2
		this.start()
	}

	public void start() {
		StartMessage messageHorizontal = new StartMessage(
				vPlayer.getPlayerName(), true, data.getArray())
		StartMessage messageVertical = new StartMessage(
				hPlayer.getPlayerName(), false, data.getArray())

		listener.notifyStart()

		hPlayer.sendMessage(messageHorizontal)
		vPlayer.sendMessage(messageVertical)

		this.started = true
	}

	/**
	 * @param cellIndex
	 *            index of the cell in the current row/column
	 * @return ErrorType.CORRECT iff the move is possible, otherwise an other
	 *         error code
	 */
	public int canPlay(boolean fromPlayerHorizontal, int x, int y) {
		if (fromPlayerHorizontal != horizontal) {
			return ErrorType.NOT_YOUR_TURN
		}
		if (horizontal) {
			if (y != current) {
				return ErrorType.BAD_ROW_COLUMN
			}
		} else {
			if (x != current) {
				return ErrorType.BAD_ROW_COLUMN
			}
		}
		int currValue = data.getDataAt(x, y)
		if (currValue == CellConstant.START) {
			return ErrorType.START_CELL
		}
		if (currValue == CellConstant.TAKEN) {
			return ErrorType.ALREADY_TAKEN
		}

		return ErrorType.CORRECT
	}

	/**
	 * @param cellIndex
	 *            index of the cell in the current row/column
	 * @return true iff the move was done
	 */
	public int play(boolean fromPlayerHorizontal, int x, int y) {
		int result = canPlay(fromPlayerHorizontal, x, y)
		if (result == ErrorType.CORRECT) {
			if (horizontal) {
				moves.add(x)
			} else {
				moves.add(y)
			}

			int currValue = data.getDataAt(x, y)
			if (fromPlayerHorizontal) {
				scoreH += currValue
			} else {
				scoreV += currValue
			}
			if (horizontal) {
				horizontal = false
				current = x
			} else {
				horizontal = true
				current = y
			}
			data.setDataAt(x, y, CellConstant.TAKEN)

			boolean hasData
			if (horizontal) {
				hasData = data.hasDataRow(y)
			} else {
				hasData = data.hasDataColumn(x)
			}
			if (hasData) {
				result = ErrorType.CORRECT
			} else {
				result = ErrorType.END
			}
		}
		return result
	}

	public int getScoreH() {
		return scoreH
	}

	public int getScoreV() {
		return scoreV
	}

	public void commandPlayMove(int id, int x, int y) {
		if (hPlayer.getPlayerId() == id) {
			_commandPlay(hPlayer, vPlayer, true, x, y)
		} else if (vPlayer.getPlayerId() == id) {
			_commandPlay(vPlayer, hPlayer, false, x, y)
		} else {
			log.info("Message commandPlay ignored from id: " + id)
		}
	}

	private void _commandPlay(IServerPlayerProxy player,
	IServerPlayerProxy other, boolean isHorizontal, int x, int y) {
		int error = play(isHorizontal, x, y)
		IMessage message = new NotifyMoveMessage(x, y, error, scoreH, scoreV)

		player.sendMessage(message)
		if (error == ErrorType.CORRECT) {
			other.sendMessage(message)
		} else if (error == ErrorType.END) {
			other.sendMessage(message)

			this.ended = true
			listener.notifyEnd(scoreH, scoreV)
		}
	}

	public void commandServerChat(int id, String content) {
		IServerPlayerProxy player
		if(hPlayer.getPlayerId() == id){
			player = hPlayer
		}else if(vPlayer.getPlayerId() == id){
			player = vPlayer
		}

		if(player){
			IMessage message = new ChatToClientMessage(content, player.getPlayerName())
			hPlayer.sendMessage(message)
			vPlayer.sendMessage(message)
		} else {
			log.info("Message commandChat ignored from id: " + id)
		}
	}

	public void notifyPlayerLeft(IServerPlayerProxy player){
		if(hPlayer == player){
			
		}else{
		
		}
		this.ended = true
	}
	
	/**
	 * @return The list of move, only when ended, null otherwise
	 */
	public List<Integer> getListOfMoves() {
		if (ended) {
			return moves
		} else {
			return null
		}
	}
	
	public int getSeed(){
		return data.getSeed()
	}
}
