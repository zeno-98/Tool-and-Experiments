package learn.defaultteacher;

import learn.frame.EquivalenceQuery;
import lombok.Data;
import ta.TA;
import ta.TAUtil;
import ta.TaLocation;
import ta.TimeGuard;
import ta.dbm.ActionGuard;
import ta.dbm.DBMUtil;
import ta.dbm.TransitionState;
import ta.ota.DOTA;
import ta.ota.LogicTimeWord;
import ta.ota.LogicTimedAction;
import ta.ota.ResetLogicTimeWord;

import java.util.List;

@Data
public class DefaultEquivalenceQuery implements EquivalenceQuery<ResetLogicTimeWord, DOTA> {
    private DOTA dota;
    private int count;

    public DefaultEquivalenceQuery(DOTA dota) {
        this.dota = dota;
    }

    @Override
    public ResetLogicTimeWord findCounterExample(DOTA hypothesis) {
        count++;
        TA negDota = TAUtil.negTA(dota);
        TA negHypothesis = TAUtil.negTA(hypothesis);

        TA ta1 = TAUtil.parallelCombination(dota, negHypothesis);
        TA ta2 = TAUtil.parallelCombination(negDota, hypothesis);

        List<TransitionState> stateTrace1 = TAUtil.reachable(ta1);
        List<TransitionState> stateTrace2 = TAUtil.reachable(ta2);

        List<TransitionState> states = shortTrace(stateTrace1, stateTrace2);
        if (states == null) {
            return null;
        }
        return analyze(states, hypothesis);

    }

    @Override
    public int getCount() {
        return count;
    }

    public List<TransitionState> shortTrace(List<TransitionState> states1, List<TransitionState> states2) {
        if (null == states1 && null == states2) {
            return null;
        }
        if (null == states1) {
            return states2;
        }

        if (null == states2) {
            return states1;
        }
        return states1.size() <= states2.size() ? states1 : states2;
    }

    public ResetLogicTimeWord analyze(List<TransitionState> stateList, DOTA hypothesis) {
        List<ActionGuard> actionGuards = DBMUtil.transfer(stateList);

//        for(ActionGuard actionGuard: actionGuards){
//            System.out.println(actionGuard.getSymbol()+" "+actionGuard.getTimeGuard());
//        }

        //初始化最小反例
        LogicTimeWord logicTimeWord = LogicTimeWord.emptyWord();
        for (ActionGuard actionGuard : actionGuards) {
            LogicTimedAction logicAction = new LogicTimedAction(
                    actionGuard.getSymbol(), actionGuard.getLowerValue());
            logicTimeWord = logicTimeWord.concat(logicAction);
        }

        //反例判断
        ex:
        while (true) {
//            System.out.println(logicTimeWord);
            ResetLogicTimeWord resetLogicTimeWord1 = dota.transferReset(logicTimeWord);
            ResetLogicTimeWord resetLogicTimeWord2 = hypothesis.transferReset(logicTimeWord);
            if (!resetLogicTimeWord1.equals(resetLogicTimeWord2)) {
                return resetLogicTimeWord1;
            }
            TaLocation a1 = dota.reach(logicTimeWord);
            TaLocation a2 = hypothesis.reach(logicTimeWord);

            if (a1.isAccept() != a2.isAccept()) {
                return dota.transferReset(logicTimeWord);
            }
            ex2:
            for (int i = 0; i < logicTimeWord.size(); i++) {
                LogicTimedAction logicAction = logicTimeWord.get(i);
                ActionGuard actionGuard = actionGuards.get(i);
                TimeGuard guard = actionGuards.get(i).getTimeGuard();
                double value = logicAction.getValue();
                if (value == (int) value) {
                    value += 0.5;
                    if (guard.isPass(value)) {
                        logicAction.setValue(value);
                        continue ex;
                    }
                    value = actionGuard.getLowerValue();
                    logicAction.setValue(value);
                    continue ex2;
                }
                value = (int) (value + 1);
                if (guard.isPass(value)) {
                    logicAction.setValue(value);
                    continue ex;
                }
                value = actionGuard.getLowerValue();
                logicAction.setValue(value);
                continue ex2;
            }
            throw new RuntimeException("搜索不到反例，请检查代码");
        }
    }


}
