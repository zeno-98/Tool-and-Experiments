package verification.frame;


import learn.frame.EquivalenceQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ta.TimeGuard;
import ta.dbm.ActionGuard;
import ta.ota.*;
import verification.Config;
import verification.frame.checkerimpl.Promise1Checker;
import verification.frame.checkerimpl.Promise2Checker;
import verification.plugins.MinimalSigma;
import verification.uppaal.verify.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static verification.Constant.CHECK_FAILED;
import static verification.Constant.RESET_SIGMA;

public class UppaalEquivalenceQuery implements EquivalenceQuery<ResetLogicTimeWord, DOTA> {
    private final Promise1Checker promise1Checker;
    private final Promise2Checker promise2Checker;
    private final Set<String> resetSigmaSet;
    private final Set<String> targetSigmaSet;
    private List<ResetLogicTimeWord> promise1CounterExamples;
    private List<ResetLogicTimeWord> promise2CounterExamples;
    @Setter
    private MinimalSigma sigmaSelector;
    @Getter
    @Setter
    private long delay;
    @Getter
    @Setter
    private int count;

    public UppaalEquivalenceQuery(Promise1Checker promise1Checker, Promise2Checker promise2Checker,
                                  Set<String> resetSigmaSet, Set<String> targetSigmaSet) {
        this.promise1Checker = promise1Checker;
        this.promise2Checker = promise2Checker;
        this.resetSigmaSet = resetSigmaSet;
        this.targetSigmaSet = targetSigmaSet;
        initCERecorder();
    }

    @SneakyThrows
    @Override
    public ResetLogicTimeWord findCounterExample(DOTA hypothesis) {
        count++;
        long begin = System.currentTimeMillis();
        ResetLogicTimeWord ctx = equivalenceQuery1(hypothesis);
        System.out.println("eq1:耗时 " + (System.currentTimeMillis() - begin));
        if (null != ctx) {
            return ctx;
        }
        begin = System.currentTimeMillis();
        ctx = equivalenceQuery2(hypothesis);
        System.out.println("eq2:耗时 " + (System.currentTimeMillis() - begin));
        return ctx;
    }

    //todo 考虑等价查询的反例是否可以加上target symbol的全部信号

    /**
     * 判断组合是否满足Promise1
     */
    @SneakyThrows
    private ResetLogicTimeWord equivalenceQuery1(DOTA hypothesis) {
        Result checkResult = promise1Checker.isSatisfied(hypothesis, hypothesis.getSigma());
        boolean accepted = checkResult.isSatisfy();
        if (!accepted) {
            if (hypothesis.getAcceptedLocations().size() == 0) {
                sigmaSelector.getNext();
                initCERecorder();
                return RESET_SIGMA;
            }
            ResetLogicTimeWord ctx = logicTimeWord2ResetLogicTimeWord(checkResult.getLogicTimeWord(), resetSigmaSet);
            if (Config.COMPLETED_EXAMPLE && promise2Checker.isSatisfied(ctx, targetSigmaSet).isSatisfy()) {
                System.out.println(promise1Checker.getStatement() + " 性质不满足，反例是" + ctx);
                return CHECK_FAILED;
            }

            // target sigma 和 hypothesis sigma一致，无需refine
            if (hypothesis.getSigma().containsAll(targetSigmaSet)) {
                if (!Config.COMPLETED_EXAMPLE && promise2Checker.isSatisfied(ctx, targetSigmaSet).isSatisfy()) {
                    System.out.println(promise1Checker.getStatement() + " 性质不满足，反例是" + ctx);
                    return CHECK_FAILED;
                }
                return ctx;
            }
            promise1CounterExamples.add(ctx);
            if (promise2Checker.isSatisfied(ctx, hypothesis.getSigma()).isSatisfy()) {
                // refine sigma
                findNextSigma();
                initCERecorder();
                return RESET_SIGMA;
            }

            // down set ctx and return
            return ctx;
        }
        return null;
    }

    /**
     * 判断组合是否满足Promise2
     */
    @SneakyThrows
    private ResetLogicTimeWord equivalenceQuery2(DOTA hypothesis) {
        //如果本身无sink状态，说明语言是全集，必定是m2的抽象
//        if (size == hypothesis.size()) {
//            System.out.println("性质满足");
//            return null;
//        }
        Result verifyResult = promise2Checker.isSatisfied(hypothesis, hypothesis.getSigma());
        boolean answer = verifyResult.isSatisfy();

        if (!answer) {
            ResetLogicTimeWord ctx = logicTimeWord2ResetLogicTimeWord(verifyResult.getLogicTimeWord(), resetSigmaSet);

            if (Config.COMPLETED_EXAMPLE && !promise1Checker.isSatisfied(ctx, targetSigmaSet).isSatisfy()) {
                System.out.println(promise1Checker.getStatement() + " 性质不满足，反例是" + ctx);
                return CHECK_FAILED;
            }


            // target sigma 和 hypothesis sigma一致，无需refine
            if (hypothesis.getSigma().containsAll(targetSigmaSet)) {
                if (!Config.COMPLETED_EXAMPLE && !promise1Checker.isSatisfied(ctx, targetSigmaSet).isSatisfy()) {
                    System.out.println(promise1Checker.getStatement() + " 性质不满足，反例是" + ctx);
                    return CHECK_FAILED;
                }
                return ctx;
            }
            promise2CounterExamples.add(ctx);
            if (!promise1Checker.isSatisfied(ctx, hypothesis.getSigma()).isSatisfy()) {
                // refine sigma
                findNextSigma();
                initCERecorder();
                return RESET_SIGMA;
            }

            return ctx;
        } else {
            System.out.println(promise1Checker.getStatement() + " 性质满足");
            return null;
        }
    }

    private void initCERecorder() {
        promise1CounterExamples = new ArrayList<>();
        promise2CounterExamples = new ArrayList<>();
    }

    private boolean findNextSigma() {
        // refine sigma
        while (sigmaSelector.hasNext()) {
            Set<String> nextSigma = sigmaSelector.getNext();
            if (!Config.COMPLETED_EXAMPLE || checkSigma(nextSigma)) {
                return true;
            }
        }
        System.out.println("未发现符合要求的字母表, 请检查代码");
        return false;
    }

    /**
     * 检验动作集合是否可以应用到已知的所有反例中
     */
    private boolean checkSigma(Set<String> nextSigma) {
        for (ResetLogicTimeWord e1 : promise1CounterExamples) {
            if (promise2Checker.isSatisfied(e1, nextSigma).isSatisfy()) {
                return false;
            }
        }
        for (ResetLogicTimeWord e2 : promise2CounterExamples) {
            if (!promise1Checker.isSatisfied(e2, nextSigma).isSatisfy()) {
                return false;
            }
        }
        return true;
    }


    private ResetLogicTimeWord analyseCtx(List<ActionGuard> actionGuards) {

        //初始化lowbound反例
        ResetLogicTimeWord resetLogicTimeWord = ResetLogicTimeWord.emptyWord();
        for (ActionGuard actionGuard : actionGuards) {
            String symbol = actionGuard.getSymbol();
            Double value = actionGuard.getLowerValue();
            boolean reset = resetSigmaSet.contains(symbol);
            ResetLogicAction logicAction = new ResetLogicAction(symbol, value, reset);
            resetLogicTimeWord = resetLogicTimeWord.concat(logicAction);
        }
        return resetLogicTimeWord;

        //反例判断
//        point1:
//        while (true) {
//            System.out.println(resetLogicTimeWord);
//            //不满足就是反例，直接返回
//            if (checker.isNotSatisfied(VerificationUtil.traceOTA(resetLogicTimeWord))) {
//                return resetLogicTimeWord;
//            }
//            point2:
//            for (int i = 0; i < resetLogicTimeWord.size(); i++) {
//                ResetLogicAction logicAction = resetLogicTimeWord.get(i);
//                ActionGuard actionGuard = actionGuards.get(i);
//                TimeGuard guard = actionGuards.get(i).getTimeGuard();
//                double value = logicAction.getValue();
//                if (value == (int) value) {
//                    //如果当前值为整数，增加0.5变成小数区间的值
//                    value += 0.5;
//                } else {
//                    //如果当前值为小数，直接加1取整
//                    value = (int) (value + 1);
//                }
//                if (guard.isPass(value)) {
//                    //增加0.5个单位，重新检测是否反例
//                    logicAction.setValue(value);
//                    continue point1;
//                } else {
//                    //重置当前动作的时钟值，进一位继续增加
//                    value = actionGuard.getLowerValue();
//                    logicAction.setValue(value);
//                    continue point2;
//                }
//            }
//            throw new RuntimeException("搜索不到反例，请检查代码");
//        }
    }

    private ResetLogicTimeWord logicTimeWord2ResetLogicTimeWord(LogicTimeWord timeWord, Set<String> resetSigma) {
        ResetLogicTimeWord resetLogicTimeWord = ResetLogicTimeWord.emptyWord();
        for (LogicTimedAction timedAction : timeWord.getTimedActions()) {
            String symbol = timedAction.getSymbol();
            ResetLogicAction logicAction = new ResetLogicAction(
                    symbol, timedAction.getValue(), resetSigma.contains(symbol));
            resetLogicTimeWord = resetLogicTimeWord.concat(logicAction);
        }
        return resetLogicTimeWord;
    }


    private List<ActionGuard> readCounterTrace() throws IOException {
        System.out.println("请给出一个counterTrace：先给出长度n，再给出2n行输入：\n");
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(rd.readLine());
        List<ActionGuard> actionGuards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String symbol = rd.readLine().trim();
            String pattern = rd.readLine().trim();
            TimeGuard guard = new TimeGuard(pattern);
            ActionGuard node = new ActionGuard(symbol, guard);
            actionGuards.add(node);
        }
        return actionGuards;
    }
}
