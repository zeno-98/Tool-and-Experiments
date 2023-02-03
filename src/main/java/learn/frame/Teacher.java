package learn.frame;

import ta.TA;
import ta.timedword.TimedWord;

public interface Teacher<T extends TimedWord, R extends TimedWord, A extends Answer, M extends TA, W extends TimedWord> {
    A membership(T timedWord);
    R equivalence(M hypothesis);
    T transferWord(W timeWord);
}
