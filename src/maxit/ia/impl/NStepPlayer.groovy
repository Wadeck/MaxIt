package maxit.ia.impl

import groovy.transform.CompileStatic

import maxit.commons.data.Coord;
import maxit.ia.AbstractIAPlayer

import org.apache.log4j.Logger

@CompileStatic
public class NStepPlayer extends AbstractIAPlayer {
	private static Logger log = Logger.getLogger(this.class)
	private def nStep
	
	def NStepPlayer(def n = 3){
		this.nStep = n
	}
	
	@Override
	public void getNextMove(Coord coord) {
		
		def debug = []
		
		// here the real computation
		int bestIncrValue = Integer.MIN_VALUE
		int bestIncrIndex = -1
		// current value for the player if taking this index
		int currValue

		int maxOtherValue 
		// next value the other player can take with the given index
		int currOtherValue

		// with this i and the max value the other can take, we have such a difference
		int currIncrValue
		for (int i = 0; i < 8; i++) {
			maxOtherValue = Integer.MIN_VALUE
			currValue = data.getDataAt(i, lastOther)
			if(currValue > 15){
				continue
			}

			for (int j = 0; j < 8; j++) {
				if(j == lastOther){
					// the one we take in this case
					continue
				}
				currOtherValue = data.getDataAt(i, j)
				if(currOtherValue <= 15){
					if(currOtherValue > maxOtherValue){
						maxOtherValue = currOtherValue
					}
				}
			}

			currIncrValue = currValue - currOtherValue
			debug << currIncrValue

			if(currIncrValue > bestIncrValue){
				bestIncrIndex = i
				bestIncrValue = currIncrValue
			}
		}
		
		log.debug "values: "+debug +", selected: "+bestIncrValue

		nextMove(coord, bestIncrIndex)
	}
}
