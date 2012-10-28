package maxit.commons.data

import groovy.transform.CompileStatic
import java.util.Collections

@CompileStatic
public class DataFactory {

	public static ServerData create() {
		int seed = (int)(Math.random() * Integer.MAX_VALUE)
		return create(seed)
	}

	public static ServerData create(int seed) {
		List<Integer> values = [
			CellConstant.START, -9, -8, -8, -7, -7, -6, -6,
			-5, -5, -5, -5, -4, -4, -4, -3,
			-3, -3, -3, -3, -2, -2, -2, -2,
			-2, -1, -1, -1, -1, 0, 0, 0,
			0, 0, 1, 1, 1, 1, 2, 2,
			2, 2, 3, 3, 3, 3, 4, 4,
			4, 4, 5, 5, 5, 6, 6, 6,
			7, 7, 8, 8, 9, 9, 10, 15
		]
		Random ran = new Random(seed)
		
		int[] data = new int[64]

		Collections.shuffle(values, ran)
		data = values.toArray() as int[]

		int startIndex = values.findIndexOf{ it == CellConstant.START }
		int x = startIndex % 8
		int y = (int)(startIndex / 8)
		return new ServerData(data, x, y, seed)
	}
//	public static ServerData create(int seed) {
//		Random ran = new Random(seed)
//		
//		int[] data = new int[64]
//				// -9 to 15
//				List<Integer> randoms = new ArrayList<Integer>(25)
//				for (int i = 0; i < 25; i++) {
//					randoms.add(i - 9)
//				}
//		
//		for (int i = 0; i < 8; i++) {
//			Collections.shuffle(randoms, ran)
//			for (int j = 0; j < 8; j++) {
//				data[i + 8 * j] = randoms.get(j)
//			}
//		}
//		
//		int x = ran.nextInt(8)
//				int y = ran.nextInt(8)
//				
//				data[x + 8 * y] = CellConstant.START
//				
//				return new ServerData(data, x, y, seed)
//	}
}
