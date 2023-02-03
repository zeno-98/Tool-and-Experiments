package verification.frame;

import learn.defaultteacher.BooleanAnswer;
import learn.frame.Membership;
import lombok.Data;
import lombok.SneakyThrows;
import ta.ota.DOTA;
import ta.ota.ResetLogicTimeWord;
import ta.timedword.TimeWordHelper;
import verification.frame.checkerimpl.Promise1Checker;
import verification.plugins.SequenceChecker;
import verification.util.VerificationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UppaalMembership implements Membership<ResetLogicTimeWord, BooleanAnswer> {
    private Promise1Checker membershipChecker;
    private int count;
    private Map<ResetLogicTimeWord, Boolean> cache;
    //插件
    private List<SequenceChecker> plugins;

    public UppaalMembership(Promise1Checker membershipChecker) {
        this.membershipChecker = membershipChecker;
        this.cache = new HashMap<>();
    }

    public void initCache() {
        this.cache = new HashMap<>();
    }

    @SneakyThrows
    @Override
    @Deprecated
    public BooleanAnswer answer(ResetLogicTimeWord resetLogicTimeWord) {
        return null;
    }

    /**
     * 基于sigma字母表进行成员查询, 忽略掉M1中其余的同步信号
     */
    @SneakyThrows
    public BooleanAnswer answer(ResetLogicTimeWord resetLogicTimeWord, Set<String> sigmas) {
        if (!TimeWordHelper.isValidWord(resetLogicTimeWord)) {
            return new BooleanAnswer(false, true);
        }
        if (cache.containsKey(resetLogicTimeWord)) {
            return new BooleanAnswer(cache.get(resetLogicTimeWord), false);
        }
        count++;
        if (null != plugins) {
            for (SequenceChecker plugin : plugins) {
                if (!plugin.isSatisfied(resetLogicTimeWord, sigmas)) {
                    return new BooleanAnswer(false, false);
                }
            }

        }
        DOTA traceDota = VerificationUtil.traceOTA(resetLogicTimeWord);
//        boolean accepted = membershipChecker.isSatisfied(traceDota).isSatisfy();
        long begin = System.currentTimeMillis();
        boolean accepted = membershipChecker.isSatisfied(traceDota, sigmas).isSatisfy();
        System.out.println("membership查询:耗时 " + (System.currentTimeMillis() - begin) + resetLogicTimeWord);
        cache.put(resetLogicTimeWord, accepted);
        return new BooleanAnswer(accepted, false);
    }

    @Override
    public int getCount() {
        return count;
    }
}
