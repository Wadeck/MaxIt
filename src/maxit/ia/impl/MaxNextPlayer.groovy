package maxit.ia.impl

import groovy.transform.CompileStatic
import maxit.commons.data.Coord
import maxit.ia.AbstractIAPlayer

import org.apache.log4j.Logger

@CompileStatic
public class MaxNextPlayer extends AbstractIAPlayer {
	private static Logger log = Logger.getLogger(this.class)
	@Override
	public void getNextMove(Coord coord) {
		def debug = []
		// here the real computation
		int maxValue = Integer.MIN_VALUE
		int maxIndex = -1
		int currValue
		for (int i = 0; i < 8; i++) {
			currValue = data.getDataAt(i, lastOther)
			if(currValue <= 15){
				if(currValue > maxValue){
					maxIndex = i
					maxValue = currValue
				}
				debug << currValue
			}
		}
//		log.debug "values: "+debug +", selected: "+maxValue

		nextMove(coord, maxIndex)
	}
}
