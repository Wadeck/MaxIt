package maxit.commons.data;

import groovy.transform.CompileStatic;

@CompileStatic
public class SimpleData implements IData{
	private int[] data;

	public SimpleData(int[] data) {
		this.data = data;
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
	 * @param x
	 *            [0..8[
	 * @param y
	 *            [0..8[
	 */
	public void setDataAt(int x, int y, int value) {
		data[x + 8 * y] = value;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		String value;
		int curr;
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				curr = data[i + 8 * j];
				if (curr == 16) {
					value = " ";
				} else if (curr == 17) {
					value = " ";
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
}
