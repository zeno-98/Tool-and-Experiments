package ta.timedword;

import ta.ota.ResetLogicAction;
import ta.ota.ResetLogicTimeWord;

public class TimeWordHelper {

    public static boolean isValidWord(ResetLogicTimeWord resetLogicTimeWord) {
        double value = 0d;
        for (ResetLogicAction action : resetLogicTimeWord.getTimedActions()) {
            double v = action.getValue();
            if (v < value) {
                return false;
            }
            value = v;
            if (action.isReset()) {
                value = 0;
            }
        }
        return true;
    }
}
