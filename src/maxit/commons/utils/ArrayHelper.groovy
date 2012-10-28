package maxit.commons.utils;

import groovy.transform.CompileStatic;

@CompileStatic
public class ArrayHelper {
	/**
	 * Typical usage: join(listOfUsers, ", ");
	 * @param array
	 * @param glue The element that will be added between every elements of the array
	 * @return The concatenation of all elements with the glue
	 */
	public static String join(int[] array, String glue) {
		StringBuilder sb = new StringBuilder();
		int size = array.length - 1;
		int i = 0;
		for (; i < size; i++) {
			sb.append(array[i]);
			sb.append(glue);
		}
		sb.append(array[i]);

		return sb.toString();
	}
}
