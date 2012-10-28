package maxit.commons.data

import groovy.transform.CompileStatic

@CompileStatic
class ServerData implements IData{
	private int[] data
	private final int startX
	private final int startY
	private int seed

	public ServerData(int[] data, int startX, int startY, int seed = 0) {
		this.data = data
		this.startX = startX
		this.startY = startY
		this.seed = seed
	}

	/**
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 * @return
	 */
	public int getDataAt(int x, int y) {
		return data[x + 8 * y]
	}

	/**
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 */
	public void setDataAt(int x, int y, int value) {
		data[x + 8 * y] = value
	}

	public int getStartX() {
		return startX
	}

	public int getStartY() {
		return startY
	}

	public boolean hasDataRow(int y) {
		for (int i = 0; i < 8; i++) {
			if (data[i + 8 * y] <= 15) {
				return true
			}
		}
		return false
	}

	public boolean hasDataColumn(int x) {
		for (int j = 0; j < 8; j++) {
			if (data[x + 8 * j] <= 15) {
				return true
			}
		}
		return false
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder()
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				buffer.append(data[i + 8 * j] + "\t")
			}
			buffer.append("\n")
		}
		return buffer.toString()
	}

	public int[] getArray() {
		return data
	}
	
	public int getSeed(){
		return seed
	}
}
