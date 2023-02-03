package verification.plugins;

import lombok.Data;
import lombok.Getter;
import ta.ota.ResetLogicAction;
import ta.ota.ResetLogicTimeWord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class SequenceChecker {

    private boolean and;
    @Getter
    private Set<String> asserts;

    public SequenceChecker(boolean and) {
        this.and = and;
    }

    /**
     * 校验给定的逻辑时间字是否符合
     *
     * @param text
     * @param resetLogicTimeWord
     * @return
     */
    private static boolean isSatisfied(String text, ResetLogicTimeWord resetLogicTimeWord, Set<String> downSets) {
        String[] array = text.split(":");
        String validType = array[0];
        String validExpress = array[1];
        String[] words = validExpress.trim().substring(1, validExpress.length() - 1).split(",");
        List<ResetLogicAction> resetLogicActionList = resetLogicTimeWord.getTimedActions();
        List<String> validWords = Arrays.stream(words).map(String::trim).filter(downSets::contains).collect(Collectors.toList());
        if (validType.equals("[circle]")) {
            return circleCheck(validWords, resetLogicActionList);
        }
        if (validType.equals("[startWith]")) {
            return startWithCheck(validWords, resetLogicActionList);
        }
        if (validType.equals("[beforeAfter]")) {
            return beforeAfterCheck(validWords, resetLogicActionList);
        }
        return false;
    }

    private static boolean beforeAfterCheck(List<String> validWords, List<ResetLogicAction> resetLogicActionList) {
        Set<String> validWordSet = new HashSet<>(validWords);
        if (validWords.size() <= 1) return true;
        int idx = 0;
        String nextAction = validWords.get(idx);
        validWordSet.remove(nextAction);
        for (ResetLogicAction action : resetLogicActionList) {
            if (validWordSet.contains(action.getSymbol())) {
                return false;
            }
            if (nextAction.equals(action.getSymbol())) {
                idx++;
                if (idx >= validWords.size() - 1) return true;
                nextAction = validWords.get(idx);
                validWordSet.remove(nextAction);
            }
        }
        return true;
    }

    private static boolean circleCheck(List<String> validWords, List<ResetLogicAction> resetLogicActionList) {
        int len = validWords.size();
        if (validWords.size() == 0) return true;
        for (int i = 0; i < resetLogicActionList.size(); i++) {
            String action = validWords.get(i % len);
            ResetLogicAction resetLogicAction = resetLogicActionList.get(i);
            if (!action.equals(resetLogicAction.getSymbol())) {
                return false;
            }
        }
        return true;
    }

    private static boolean startWithCheck(List<String> validWords, List<ResetLogicAction> resetLogicActionList) {
        int len = validWords.size();
        for (int i = 0; i < len && i < resetLogicActionList.size(); i++) {
            String action = validWords.get(i);
            ResetLogicAction resetLogicAction = resetLogicActionList.get(i);
            if (!action.equals(resetLogicAction.getSymbol())) {
                return false;
            }
        }
        return true;
    }

    public boolean isSatisfied(ResetLogicTimeWord resetLogicTimeWord, Set<String> downSets) {
        for (String s : asserts) {
            boolean checkRes = isSatisfied(s, resetLogicTimeWord, downSets);
            if (!and && checkRes) {
                return true;
            }
            if (and && !checkRes) {
                return false;
            }
        }
        return and;
    }

    public void add(String s) {
        if (asserts == null) {
            asserts = new HashSet<>();
        }
        asserts.add(s);
    }
}
