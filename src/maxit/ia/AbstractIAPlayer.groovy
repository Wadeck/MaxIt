package maxit.ia

import groovy.transform.CompileStatic
import maxit.commons.core.IArtificialPlayer
import maxit.commons.data.CellConstant
import maxit.commons.data.Coord
import maxit.commons.data.IAData

@CompileStatic
abstract class AbstractIAPlayer implements IArtificialPlayer {
	protected IAData data
	protected boolean isHori
	// coordinates related with the isHori, meaning they could be reversed
	/** The last index for me */
	protected int lastMine
	/** The last index for the other, could be row or column */
	protected int lastOther

	@Override
	public void initData(int[] data, boolean isHori) {
		this.data = new IAData(data, isHori)
		this.isHori = isHori

		this.lastMine = this.data.getStartX()
		this.lastOther = this.data.getStartY()
	}

	@Override
	public void notifyEnd() {
	}

	@Override
	public void notifyMyMove(int x, int y) {
		data.setRealDataAt(x, y, CellConstant.TAKEN)

		if(isHori){
			this.lastMine = x
			this.lastOther = y
		}else{
			this.lastOther = x
			this.lastMine = y
		}
	}

	@Override
	public void notifyOtherMove(int x, int y) {
		data.setRealDataAt(x, y, CellConstant.TAKEN)

		if(isHori){
			this.lastMine = x
			this.lastOther = y
		}else{
			this.lastOther = x
			this.lastMine = y
		}
	}

	protected void nextMove(Coord coord, int index){
		if(isHori){
			coord.x = index
			coord.y = lastOther
		}else{
			coord.x = lastOther
			coord.y = index
		}
	}
	
	@Override
	String toString() { this.class.getSimpleName() }
}
