package verification.frame;

import learn.frame.Learner;
import lombok.Data;
import ta.ota.DOTA;
import ta.ota.ResetLogicTimeWord;
import verification.plugins.MinimalSigma;
import verification.teacher.UppaalTeacher;

import java.util.Set;

import static verification.Constant.RESET_SIGMA;

@Data
public class CheckFrame {
    private UppaalTeacher teacher;
    private Learner<ResetLogicTimeWord> learner;
    private boolean guessAlphabet = false;
    private MinimalSigma sigmaSelector;

    public CheckFrame(UppaalTeacher teacher, Learner<ResetLogicTimeWord> learner) {
        this.teacher = teacher;
        this.learner = learner;
    }

    public void guessSigmas(Set<String> targetSigma) {
        guessAlphabet = true;
        sigmaSelector = new MinimalSigma(targetSigma);
        teacher.setSigmaSelector(sigmaSelector);
    }

    public void start() {
        long start = System.currentTimeMillis();
        execute();
        long end = System.currentTimeMillis();

        System.out.println("学习结束\n"
                + "总耗时：" + (end - start) + " ms\n"
                + "输入耗时：" + teacher.getDelayTime() + " ms\n"
                + "验证耗时：" + (end - start - teacher.getDelayTime()) + " ms\n"
                + "------------------------------");
    }

    public void start(int repeatCount) {
        long totalTime = 0;
        for (int i = 0; i < repeatCount; i++) {
            long start = System.currentTimeMillis();
            execute();
            long end = System.currentTimeMillis();
            totalTime += end - start;
        }

        System.out.println("学习结束\n"
                + "平均耗时：" + totalTime / repeatCount + " ms\n"
                + "输入平均耗时：" + teacher.getDelayTime() / repeatCount + " ms\n"
                + "验证平均耗时：" + (totalTime / repeatCount - teacher.getDelayTime() / repeatCount) + " ms");
    }

    private void execute() {
        long begin = System.currentTimeMillis();
        //1、学习者初始化
        if (guessAlphabet) {
            sigmaSelector.init();
            learner.init(sigmaSelector.getNext());
        } else {
            learner.init();
        }
        //2、开始学习
        learner.learn();
        System.out.println("学习初始化耗时 " + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();

        learner.show();
        //3、生成假设
        DOTA hypothesis = learner.buildHypothesis();
        System.out.println(hypothesis);
        System.out.println("构建假设自动机耗时 " + (System.currentTimeMillis() - begin));
        //4、等价判断
        ResetLogicTimeWord ce = null;
        while (null != (ce = teacher.equivalence(hypothesis))) {
            if (RESET_SIGMA.equals(ce)) {
                Set<String> curSigma = sigmaSelector.getCur();
                System.out.println("重构字母表为" + curSigma);
                learner.init(curSigma);
                learner.learn();
                learner.show();
            } else {
                System.out.println("反例是：" + ce);
                learner.refine(ce);
                learner.show();
            }

            hypothesis = learner.buildHypothesis();
            System.out.println(hypothesis);
        }
    }

}
