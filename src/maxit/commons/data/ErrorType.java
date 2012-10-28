package maxit.commons.data;

public class ErrorType {
	/** Correct move */
	public static final int CORRECT = 0;
	/** Bad row or column */
	public static final int BAD_ROW_COLUMN = 1;
	/** Can't play on start cell */
	public static final int START_CELL = 2;
	/** This cell is already taken */
	public static final int ALREADY_TAKEN = 3;
	/** Not your turn */
	public static final int NOT_YOUR_TURN = 4;
	/** Finish ! */
	public static final int END = 5;

	public static String getMessage(int code) {
		switch (code) {
			case CORRECT:
				return "Correct move";
			case BAD_ROW_COLUMN:
				return "Bad row or column";
			case START_CELL:
				return "Can't play on start cell";
			case ALREADY_TAKEN:
				return "This cell is already taken";
			case NOT_YOUR_TURN:
				return "Not your turn";
			case END:
				return "Finish !";
		}
		return null;
	}
}
