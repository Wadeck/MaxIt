package maxit.commons.data;

public class CellConstant {
    public static int START = 16 ;
    public static int TAKEN = 17 ;

    public static boolean isPlayableValue(int value) {
        return value <= 15;
    }
}
