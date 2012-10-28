package maxit.commons.data;

public class IAData {
	private int[] data;
	private int startX;
	private int startY;
	private final boolean isHori;

	public IAData(int[] _data, boolean _isHori) {
		this.isHori = _isHori;
		if (_isHori) {
			this.data = _data;
		} else {
			this.data = new int[_data.length];
			int index
			for (int i = 0; i < _data.length; i++) {
				index = (i / 8) + 8 * (i % 8)
				this.data[i] = _data[index];
			}
		}
		for (int i = 0; i < _data.length; i++) {
			if (_data[i] == CellConstant.START) {
				startX = i % 8;
				startY = i / 8;
				break;
			}
		}
	}

	/**
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 * @return
	 */
	public int getDataAt(int x, int y) {
		return data[x + 8 * y];
	}

	/**
	 * Use when we have real x and y
	 *
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 * @return
	 */
	public int getRealDataAt(int x, int y) {
		if (isHori) {
			return data[x + 8 * y];
		} else {
			return data[y + 8 * x];
		}
	}

	/**
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 */
	public void setDataAt(int x, int y, int value) {
		data[x + 8 * y] = value;
	}

	/**
	 * Use when we have real x and y
	 *
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 */
	public void setRealDataAt(int x, int y, int value) {
		if (isHori) {
			data[x + 8 * y] = value;
		} else {
			data[y + 8 * x] = value;
		}
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		String value;
		int curr;
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				curr = data[i + 8 * j];
				if (curr == CellConstant.START) {
					value = "#";
				} else if (curr == CellConstant.TAKEN) {
					value = ".";
				} else {
					value = curr + "";
				}
				buffer.append(value + "\t");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public int[] getArray() {
		return data;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}
}
