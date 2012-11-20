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
public class BestStepPlayer extends AbstractIAPlayer {
	private static Logger log = Logger.getLogger(this.class)
	
	private static final boolean DEBUG = false
	/** Number of turn in advance */
	private int nStep
	private int numChoices

	// n = 0 => maxNext
	// n = 1 => minMax	
	def BestStepPlayer(int n = 7, int c = 4){
		this.nStep = n 
		this.numChoices = c
	}
	
	def BestStepPlayer(String n, String c){
		this(n.toInteger(), c.toInteger())
	}
	
	/** Current state of which cells are taken */
	private boolean[][] taken
	/** Current difference */
	private int difference
	/** Values of each columns/rows cells during the search */
	private List<short[]> computedValues
	
	private int currentStep
	
	@Override
	public void getNextMove(Coord coord) {
		if(DEBUG){
			log.debug "starting computation... with current state:\n${ this.data.toString() }"
		}
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
	
	private int computeMinValueToBeChosen(int rowCol, boolean isHori){
		// determine which cells we want to follow
		// the total number of possible cell
		int totalChoices = 0;
		// the number we want to use
		int numOfChoices = 0;
		// the minimum value to have to be followed
		// in order to solve the situation where 2 cells are equals
		int minToBeChosen = Integer.MIN_VALUE;
		
		def allValues = new int[8]
		
		// first step, we compute which cells can be chosen for the second step
		if(isHori){
			// each column
			for(int i = 0; i < 8 ; i++){
				if(isAlreadyUsed(i, rowCol)){
					allValues[i] = Integer.MIN_VALUE
				}else{
					allValues[i] = data.getDataAt(i, rowCol)
					totalChoices++
				}
			}
		}else{
			// each row
			for(int j = 0; j < 8 ; j++){
				if(isAlreadyUsed(rowCol, j)){
					allValues[j] = Integer.MIN_VALUE
				}else{
					allValues[j] = data.getDataAt(rowCol, j)
					totalChoices++
				}
			}
		}
		
		numOfChoices = getNumOfChoice(totalChoices)
		// find the minimum value to have to be chosen
		Arrays.sort(allValues)
		minToBeChosen = allValues[allValues.length - numOfChoices]
		
		return minToBeChosen
	}
	
	/**
	 * The first method is not findMax because we want to find the index
	 * and not the real value of the best row
	 * @param row
	 * @return index
	 */
	private int findBestChoice(int row){
		this.currentStep = 0
		def debug = new int[8]
		def curr = new short[8]
		computedValues.push(curr)
		
		def minToBeChosen = computeMinValueToBeChosen(row, true)
		
		def currValue
		for(int i = 0; i < 8 ; i++){
			currValue = data.getDataAt(i, row)
			debug[i] = currValue
			// loop on the row cells and use only the cells that were not already used
			if(isAlreadyUsed(i, row) || currValue < minToBeChosen){
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
		if(DEBUG){
			log.info "best=>${debug} diff=${difference}\n${ debugStep() }"
		}
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
		
		def minToBeChosen = computeMinValueToBeChosen(col, false)
		
		def currValue
		for(int i = 0; i < 8 ; i++){
			currValue = data.getDataAt(col, i)
			debug[i] = currValue
			// loop on the row cells and use only the cells that were not already used
			if(isAlreadyUsed(col, i) || currValue < minToBeChosen){
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
		if(DEBUG){
			log.debug "min=>${debug} diff=${difference}\n${ debugStep() }"
		}
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
		
		def minToBeChosen = computeMinValueToBeChosen(row, true)
		
		def currValue
		for(int i = 0; i < 8 ; i++){
			currValue = data.getDataAt(i, row)
			debug[i] = currValue
			// loop on the row cells and use only the cells that were not already used
			if(isAlreadyUsed(i, row) || currValue < minToBeChosen){
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
		if(DEBUG){
			log.debug "max=>${debug} diff=${difference}\n${ debugStep() }"
		}
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
	
	/**
	 * Compute the number of choice we want to investigate
	 * @param total
	 */
	private int getNumOfChoice(int total){
//		return Math.max(3, (int)(total * 0.5))
		return 3
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
	
	@Override
	String toString(){
		return this.class.simpleName + "@${ nStep }-${ numChoices }"
	}
}
