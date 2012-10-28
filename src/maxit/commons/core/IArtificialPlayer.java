package maxit.commons.core;

import maxit.commons.data.Coord;

public interface IArtificialPlayer {
	public void initData(int[] data, boolean isHori);

	public void notifyEnd();

	public void notifyMyMove(int x, int y);

	public void notifyOtherMove(int x, int y);

	/**
	 * @param coord
	 *            OUT
	 */
	public void getNextMove(Coord coord);
}