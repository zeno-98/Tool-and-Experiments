package verification;

import ta.ota.ResetLogicAction;
import ta.ota.ResetLogicTimeWord;

public class Constant {
    public final static ResetLogicTimeWord RESET_SIGMA = ResetLogicTimeWord.emptyWord().concat(new ResetLogicAction("RESET_SIGMA", 0d, true));
    public final static ResetLogicTimeWord CHECK_FAILED = ResetLogicTimeWord.emptyWord().concat(new ResetLogicAction("CHECK_FAILED", 0d, true));
}
