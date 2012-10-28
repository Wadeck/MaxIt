package maxit.commons.logic.server

import groovy.transform.CompileStatic
import maxit.commons.core.IArtificialPlayer
import maxit.commons.core.IServerPlayerProxy
import maxit.commons.data.CellConstant
import maxit.commons.data.Coord
import maxit.commons.data.DataFactory
import maxit.commons.data.ErrorType
import maxit.commons.data.ServerData
import maxit.commons.network.message.clientGame.ChatToClientMessage
import maxit.commons.network.message.clientGame.NotifyMoveMessage
import maxit.commons.network.message.type.IMessage
import maxit.ia.AbstractIAPlayer

import org.apache.log4j.Logger

@CompileStatic
public class TournamentLogic {
	private static Logger log = Logger.getLogger(this.class)

	private int[] initialData
	private ServerData data
	private int current
	private boolean horizontal

	private int scoreH, scoreV

	private IArtificialPlayer hPlayer
	private IArtificialPlayer vPlayer

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
	public TournamentLogic(IArtificialPlayer hPlayer, IArtificialPlayer vPlayer, int seed) {
		this.init(seed)

		this.hPlayer = hPlayer
		this.vPlayer = vPlayer
	}

	private void init(int seed) {
		this.started = false
		this.ended = false

		this.data = DataFactory.create(seed)
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

	public void start() {
		hPlayer.initData(data.getArray(), true)
		vPlayer.initData(data.getArray(), false)

		IArtificialPlayer current = hPlayer
		boolean fromHori = true
		Coord tempCoord = new Coord()
		int error
		
		int i = 0
		while(i < 64){
			if(i == 18){
				def d = 3
			}
//			log.debug "turn=${i}"
			current.getNextMove(tempCoord)
//			log.debug "${fromHori ? 'nstep' : 'random' } plays (${tempCoord.x}:${tempCoord.y})"
			error = play(fromHori, tempCoord.x, tempCoord.y)
			if(error != ErrorType.CORRECT){
				if(error == ErrorType.END){
					// end of the game
					return
				}else{
					throw new RuntimeException("Problem error=${error} i=${i} during move of player: ${current}")
				}
			}
			current.notifyMyMove(tempCoord.x, tempCoord.y)
			current = getOtherPlayer(current)
			current.notifyOtherMove(tempCoord.x, tempCoord.y)
			
			fromHori = !fromHori
			
			
			i++
		}
	}

	private IArtificialPlayer getOtherPlayer(IArtificialPlayer p){
		if(hPlayer == p){
			return vPlayer
		}else if(vPlayer == p){
			return hPlayer
		}else{
			return null
		}
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
