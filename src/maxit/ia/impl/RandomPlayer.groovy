package maxit.ia.impl

import groovy.transform.CompileStatic
import maxit.commons.data.Coord
import maxit.ia.AbstractIAPlayer

import org.apache.log4j.Logger

@CompileStatic
public class RandomPlayer extends AbstractIAPlayer {
	private static Logger log = Logger.getLogger(this.class)

	private Random ran

	public RandomPlayer() {
		this.ran = new Random(0)
	}

	@Override
	public void getNextMove(Coord coord) {
		int[] possibleMove = new int[8]
		int numMove = 0

		// here the real computation
		for (int i = 0; i < 8; i++) {
			if (data.getDataAt(i, lastOther) <= 15) {
				possibleMove[numMove] = i
				numMove++
			}
		}
		nextMove(coord, possibleMove[ran.nextInt(numMove)])
	}
}
