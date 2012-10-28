package maxit.ia.impl

import groovy.transform.CompileStatic
import maxit.commons.data.Coord
import maxit.ia.AbstractIAPlayer

import org.apache.log4j.Logger

import sun.org.mozilla.javascript.internal.ast.ForInLoop;

/**
 * Given a column we compute the value of each row and play the cell that maximize the value of the difference
 * @author Wadeck
 *
 */
@CompileStatic
public class NStepPlayer extends AbstractIAPlayer {
	private static Logger log = Logger.getLogger(this.class)
	/** Number of turn in advance */
	private def nStep

	// n = 0 => maxNext
	// n = 1 => minMax	
	def NStepPlayer(def n = 2){
		this.nStep = n
	}
	
	/** Current state of which cells are taken */
	private boolean[][] taken
	/** Current difference */
	private int difference
	/** Values of each columns/rows cells during the search */
	private List<short[]> computedValues
	
//	private int turn = 0
	
	@Override
	public void getNextMove(Coord coord) {
//		turn++
//		if(turn < 15){
//			def ran = new Random()
//			// first turns, just play randomly
//			int[] possibleMove = new int[8]
//			int numMove = 0
//	
//			// here the real computation
//			for (int i = 0; i < 8; i++) {
//				if (data.getDataAt(i, lastOther) <= 15) {
//					possibleMove[numMove] = i
//					numMove++
//				}
//			}
//			nextMove(coord, possibleMove[ran.nextInt(numMove)])
//			return
//		}
		
//		log.debug "starting computation... with current state:\n${ this.data.toString() }"
		init()
//		log.debug "taken:\n${ debugState() }"
		
		def indexToPlay = findBestChoice(lastOther)
		
		nextMove(coord, indexToPlay)
	}
	
	/**
	 * Use the internal data to know the current state of the game
	 * Especially which cells were taken previously
	 */
	private init(){
		this.taken = new boolean[8][8]
		(0..7).each{ int c ->
			(0..7).each{ int r ->
				if(data.getDataAt(c, r) <= 15){
					taken[c][r] = false
				}else{
					taken[c][r] = true
				}
			}
		}
		this.computedValues = []
		this.difference = 0
	}
	
	/**
	 * The first method is not findMax because we want to find the index
	 * and not the real value of the best row
	 * @param row
	 * @return index
	 */
	private int findBestChoice(int row){
		def debug = new int[8]
		def curr = new short[8]
		computedValues.push(curr)
		
		for(int i = 0; i < 8 ; i++){
			debug[i] = data.getDataAt(i, row)
			// loop on the row cells and use only the cells that were not already used
			if(isAlreadyUsed(i, row)){
				// we search max
				curr[i] = Short.MIN_VALUE
			}else{
				// we can use this cell
				useCell(i, row, true)
				// next step of the tree search
				curr[i] = findMin(i)
				releaseCell(i, row, true)
			}
		}
		// find the max value to determine the index
		def max = Short.MIN_VALUE
		def resultIndex = -1
		for(int i = 0; i < 8 ; i++){
			if(curr[i] > max){
				max = curr[i]
				resultIndex = i
			}
		}
//		log.info "best=>${debug} diff=${difference}\n${ debugStep() }"
//		log.info "final values: ${ computedValues }"
		// remove the column added
		computedValues.pop()
		
		return resultIndex
	}
	
	/**
	 * Find the cell index that minimize the difference
	 * @param col Index of the column
	 * @return value of the best row
	 */
	private short findMin(int col){
		if(computedValues.size() > nStep){
			return difference
		}
		def debug = new int[8]
		// store the values of each column
		def curr = new short[8]
		computedValues.push(curr)
		for(int i = 0; i < 8 ; i++){
			debug[i] = data.getDataAt(col, i)
			// loop on the row cells and use only the cells that were not already used
			if(isAlreadyUsed(col, i)){
				// we search max
				curr[i] = Short.MAX_VALUE
			}else{
				// we can use this cell
				useCell(col, i, false)
				// next step of the tree search
				curr[i] = findMax(i)
				releaseCell(col, i, false)
			}
		}
//		log.debug "min=>${debug} diff=${difference}\n${ debugStep() }"
		// find the max value to determine the index
		def min = curr.min()
//		def min = Short.MAX_VALUE
//		def resultIndex = -1
//		for(int i = 0; i < 8 ; i++){
//			if(curr[i] < min){
//				min = curr[i]
//				resultIndex = i
//			}
//		}
		
		// remove the column added
		computedValues.pop()
		if(min == Short.MAX_VALUE){
			return difference
		}
		return min
	}
	
	/**
	 * Find the cell index that maximize the difference
	 * @param row Index of the row
	 * @return value of the best column
	 */
	private short findMax(int row){
		if(computedValues.size() > nStep){
			return difference
		}
		def debug = new int[8]
		// store the values of each column
		def curr = new short[8]
		computedValues.push(curr)
		for(int i = 0; i < 8 ; i++){
			debug[i] = data.getDataAt(i, row)
			// loop on the row cells and use only the cells that were not already used
			if(isAlreadyUsed(i, row)){
				// we search max
				curr[i] = Short.MIN_VALUE
			}else{
				// we can use this cell
				useCell(i, row, true)
				// next step of the tree search
				curr[i] = findMin(i)
				releaseCell(i, row, true)
			}
			
//			if(Math.random() > 0.99999){
//			}
		}
//		log.debug "max=>${debug} diff=${difference}\n${ debugStep() }"
		// find the max value to determine the index
		def max = curr.max()
//		def max = Short.MIN_VALUE
//		def resultIndex = -1
//		for(int i = 0; i < 8 ; i++){
//			if(curr[i] > max){
//				max = curr[i]
//				resultIndex = i
//			}
//		}
		
		// remove the column added
		computedValues.pop()
		if(max == Short.MIN_VALUE){
			return difference
		}
		return max
	}
	
	/**
	 * Use the computedValues to show the different step
	 * @return
	 */
	private debugStep(int currItem=-1){
		def s = computedValues.size()
		StringBuilder sb = new StringBuilder()
		def val
		for (int i = 0; i < s; i++) {
			sb << "-${i}-\t"
		}
		sb << "\n"
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < s; i++) {
				if(i == s-1 && j == currItem){
					sb << "#\t"
					continue
				}
				val = computedValues[i][j]
				if(val == Short.MIN_VALUE){
					sb << ","
				}else if(val == Short.MAX_VALUE){
					sb << "."
				}else{
					sb << "" + val
				}
				
				sb << "\t"
			}
			sb << "\n"
		}
		return sb.toString()
	}
	
	/**
	 * Determine if the cell is already used
	 */
	private boolean isAlreadyUsed(int x, int y){
		return taken[x][y]
	}
	
	/**
	 * Add the value, put a marker on the cell
	 */
	private useCell(int x, int y, boolean isHori){
		taken[x][y] = true
		if(isHori){
			this.difference += data.getDataAt(x, y)
		}else{
			this.difference -= data.getDataAt(x, y)
		}
	}
	
	/**
	 * Subtract the value and remove the market from the cell
	 */
	private releaseCell(int x, int y, boolean isHori){
		taken[x][y] = false
		if(isHori){
			this.difference -= data.getDataAt(x, y)
		}else{
			this.difference += data.getDataAt(x, y)
		}
	}
	
	private debugState(){
		StringBuilder buffer = new StringBuilder()
		String value
		boolean curr
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				curr = taken[i][j]
				if (curr) {
					value = "#"
				} else {
					value = "."
				}
				buffer.append(value + "\t")
			}
			buffer.append("\n")
		}
		return buffer.toString()
	}
//	@Override
//	public void getNextMove(Coord coord) {
//		
//		def debug = []
//				
//				// here the real computation
//				int bestIncrValue = Integer.MIN_VALUE
//				int bestIncrIndex = -1
//				// current value for the player if taking this index
//				int currValue
//				
//				int maxOtherValue 
//				// next value the other player can take with the given index
//				int currOtherValue
//				
//				// with this i and the max value the other can take, we have such a difference
//				int currIncrValue
//				for (int i = 0; i < 8; i++) {
//					maxOtherValue = Integer.MIN_VALUE
//							currValue = data.getDataAt(i, lastOther)
//							if(currValue > 15){
//								continue
//							}
//					
//					for (int j = 0; j < 8; j++) {
//						if(j == lastOther){
//							// the one we take in this case
//							continue
//						}
//						currOtherValue = data.getDataAt(i, j)
//								if(currOtherValue <= 15){
//									if(currOtherValue > maxOtherValue){
//										maxOtherValue = currOtherValue
//									}
//								}
//					}
//					
//					currIncrValue = currValue - currOtherValue
//							debug << currIncrValue
//							
//							if(currIncrValue > bestIncrValue){
//								bestIncrIndex = i
//										bestIncrValue = currIncrValue
//							}
//				}
//		
//		log.debug "values: "+debug +", selected: "+bestIncrValue
//		
//		nextMove(coord, bestIncrIndex)
//	}
	
	@Override
	String toString(){
		return this.class.simpleName + "@" + nStep
	}
}
