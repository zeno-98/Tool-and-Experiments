package verification.teacher;

import learn.defaultteacher.BooleanAnswer;
import learn.frame.Teacher;
import ta.ota.DOTA;
import ta.ota.LogicTimeWord;
import ta.ota.ResetLogicAction;
import ta.ota.ResetLogicTimeWord;
import verification.frame.UppaalEquivalenceQuery;
import verification.frame.UppaalMembership;
import verification.frame.checkerimpl.Promise1Checker;
import verification.frame.checkerimpl.Promise2Checker;
import verification.plugins.MinimalSigma;
import verification.plugins.SequenceChecker;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static verification.Constant.CHECK_FAILED;
import static verification.Constant.RESET_SIGMA;

public class UppaalTeacher implements Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> {

    private final UppaalMembership membership;
    private final UppaalEquivalenceQuery equivalenceQuery;
    private final Set<String> targetSigma;
    /**
     * action对应的重置信息
     */
    private final Set<String> resetSigma;
    private MinimalSigma sigmaSelector;

    /**
     * @param m1                组合的第一部分
     * @param m2                组合的第二部分
     * @param statement         组合需要满足的性质
     * @param globalDeclaration 全局信号定义
     * @param syncSendMap       用于构建uppaal自动机时的信号同步记录
     * @param resetSigma        所有重置的信号
     */
    public UppaalTeacher(List<Template> m1, List<Template> m2, String statement, Declaration globalDeclaration, Map<String, Boolean> syncSendMap, Set<String> resetSigma, Set<String> targetSigma) {
        this.resetSigma = resetSigma;
        this.targetSigma = targetSigma;
        Promise1Checker promise1Checker = new Promise1Checker(globalDeclaration, m1, statement, syncSendMap);
        Promise2Checker promise2Checker = new Promise2Checker(m2, syncSendMap, globalDeclaration);

        membership = new UppaalMembership(promise1Checker);
        equivalenceQuery = new UppaalEquivalenceQuery(promise1Checker, promise2Checker, resetSigma, targetSigma);
    }

    public long getDelayTime() {
        return equivalenceQuery.getDelay();
    }

    public void setSequencePlugin(List<SequenceChecker> plugins) {
        this.membership.setPlugins(plugins);
    }

    @Override
    public BooleanAnswer membership(ResetLogicTimeWord timedWord) {
        if (sigmaSelector == null) {
            return membership.answer(timedWord, targetSigma);
        } else {
            return membership.answer(timedWord, sigmaSelector.getCur());
        }

    }

    @Override
    public ResetLogicTimeWord equivalence(DOTA hypothesis) {
        long begin = System.currentTimeMillis();
        ResetLogicTimeWord answer = equivalenceQuery.findCounterExample(hypothesis);
        if (RESET_SIGMA.equals(answer)) {
            // 如果等价查询发出重置信号的命令，则需要重置membership查询的缓存
            membership.initCache();
            return answer;
        }
        if (CHECK_FAILED.equals(answer)) {
            // 发生异常的等价查询
            return null;
        }
        if (answer != null && sigmaSelector != null) {
            List<ResetLogicAction> resetLogicActions = answer.getTimedActions().stream()
                    .filter(action -> sigmaSelector.getCur().contains(action.getSymbol())).collect(Collectors.toList());
            answer = new ResetLogicTimeWord(resetLogicActions);
        }

        System.out.println("等价查询耗时 " + (System.currentTimeMillis() - begin));
        return answer;
    }

    @Override
    public ResetLogicTimeWord transferWord(LogicTimeWord timeWord) {
        List<ResetLogicAction> resetLogicActions = new ArrayList<>();
        timeWord.getTimedActions().forEach(e -> {
            String symbol = e.getSymbol();
            Double value = e.getValue();
            boolean reset = resetSigma.contains(symbol);
            ResetLogicAction resetLogicAction = new ResetLogicAction(symbol, value, reset);
            resetLogicActions.add(resetLogicAction);
        });
        return new ResetLogicTimeWord(resetLogicActions);
    }

    public void setSigmaSelector(MinimalSigma sigmaSelector) {
        this.sigmaSelector = sigmaSelector;
        equivalenceQuery.setSigmaSelector(sigmaSelector);
    }
}
