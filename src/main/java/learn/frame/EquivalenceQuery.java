package learn.frame;

import ta.TA;
import ta.timedword.TimedWord;

public interface EquivalenceQuery<T extends TimedWord, R extends TA> {
    T findCounterExample(R hypothesis);

    int getCount();
}
