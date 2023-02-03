package learn.defaultteacher;


import learn.frame.Teacher;
import lombok.Data;
import ta.ota.DOTA;
import ta.ota.LogicTimeWord;
import ta.ota.ResetLogicTimeWord;

import java.util.HashMap;
import java.util.Map;

@Data
public class DefaultTeacher implements Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> {
    private DOTA dota;
    private DefaultOTAMembership membership;
    private DefaultEquivalenceQuery equivalenceQuery;
    private Map<LogicTimeWord, ResetLogicTimeWord> map = new HashMap<>();
    private Map<ResetLogicTimeWord, BooleanAnswer> map1 = new HashMap<>();

    public DefaultTeacher(DOTA dota) {
        this.dota = dota;
        membership = new DefaultOTAMembership(dota);
        equivalenceQuery = new DefaultEquivalenceQuery(dota);
    }

    @Override
    public BooleanAnswer membership(ResetLogicTimeWord timedWord) {
        if (map1.containsKey(timedWord)){
            return map1.get(timedWord);
        }
        else {
            BooleanAnswer answer = membership.answer(timedWord);
            map1.put(timedWord, answer);
            return answer;
        }
    }

    @Override
    public ResetLogicTimeWord equivalence(DOTA hypothesis) {
        return equivalenceQuery.findCounterExample(hypothesis);
    }

    @Override
    public ResetLogicTimeWord transferWord(LogicTimeWord timeWord) {
        if (map.containsKey(timeWord)){
            return map.get(timeWord);
        }
        ResetLogicTimeWord resetLogicTimeWord = dota.transferReset(timeWord);
        map.put(timeWord,resetLogicTimeWord);
        return resetLogicTimeWord;
    }


}
