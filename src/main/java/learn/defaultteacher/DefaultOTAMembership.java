package learn.defaultteacher;


import learn.frame.Membership;
import lombok.Data;
import lombok.NoArgsConstructor;
import ta.TaLocation;
import ta.ota.DOTA;
import ta.ota.ResetLogicTimeWord;
import ta.timedword.TimeWordHelper;

@Data
@NoArgsConstructor
public class DefaultOTAMembership implements Membership<ResetLogicTimeWord, BooleanAnswer> {
    private DOTA dota;
    private int count;

    public DefaultOTAMembership(DOTA dota) {
        this.dota = dota;
    }

    @Override
    public BooleanAnswer answer(ResetLogicTimeWord timedWord) {
        count++;
        if (!TimeWordHelper.isValidWord(timedWord)) {
            return new BooleanAnswer(false, true);
        } else {
            TaLocation taLocation = dota.reach(timedWord);
            boolean accept = null != taLocation && taLocation.isAccept();
            return new BooleanAnswer(accept, false);
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}
