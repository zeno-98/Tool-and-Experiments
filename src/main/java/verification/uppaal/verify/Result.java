package verification.uppaal.verify;

import lombok.AllArgsConstructor;
import lombok.Data;
import ta.TimeGuard;
import ta.dbm.ActionGuard;
import ta.ota.LogicTimeWord;
import ta.ota.LogicTimedAction;

@Data
@AllArgsConstructor
public class Result {
    private boolean satisfy;
    private String content;
    private LogicTimeWord logicTimeWord;

    Result(String pattern) {
        this.content = pattern;
        boolean notSatisfied = content.contains("Formula is NOT satisfied");
        boolean satisfy = content.contains("Formula is satisfied");
        if (!satisfy && !notSatisfied) {
            throw new RuntimeException("性质验证出错\n" + pattern);
        }
        this.satisfy = satisfy;
        logicTimeWord = LogicTimeWord.emptyWord();
        String[] strs = content.split("\n");
        boolean inTransitionStage = false;
        boolean inStateStage = false;
        String lastAssumeTime = "";
        for (String str : strs) {
            if (str.startsWith("Transitions:")) {
                inTransitionStage = true;
                inStateStage = false;
                continue;
            }
            if (str.startsWith("State:")) {
                inTransitionStage = false;
                inStateStage = true;
                continue;
            }
            if (!inTransitionStage && !inStateStage) {
                continue;
            }

            str = str.trim();
            if (inStateStage && str.contains("assume.x=")) {
                lastAssumeTime = str;
                continue;
            }
            if (inTransitionStage) {
                if (str.startsWith("assume.")) {
                    LogicTimedAction action = new LogicTimedAction(parserExactTrans(str), parserTimedAction(lastAssumeTime));
                    logicTimeWord = logicTimeWord.concat(action);
                    inTransitionStage = false;
                }
            }


//            if (waitForState && str.contains("assume.x=")) {
//                parserTimedAction(str, logicTimeWord.get(logicTimeWord.size() - 1));
//                inTransitionStage = false;
//                inStateStage = false;
//                waitForState = false;
//            }
        }
    }

    private static Double parserTimedAction(String str) {
        String[] strs = str.split(" ");
        String target = null;
        for (String s : strs) {
            if (s.startsWith("assume.x=")) {
                target = s.replace("assume.x=", "");
                break;
            }
        }
        assert target != null;
        return Double.valueOf(target);
    }

    /**
     * 转换uppaal返回的Transition反例字符串为 信号名字
     * @param transLineStr 待转换字符串
     *                          <p>
     *                          形如：assume.assume1->assume.assume3 { x >= 0, sid[0]!, 1 }
     *                           </p>
     * @return sid[0]
     */
    private static String parserExactTrans(String transLineStr) {
        int leftIdx = transLineStr.indexOf("{");
        if (leftIdx == -1){
            return null;
        }
        transLineStr = transLineStr.substring(leftIdx + 1);
        String[] splitStrs = transLineStr.split(",");
        String symbolStr = splitStrs[1].trim();
        return symbolStr.substring(0, symbolStr.length()-1);
    }

    /**
     * 转换uppaal返回的Transition反例字符串为 ActionGuard 类型实例
     * @param transLineStr 待转换字符串
     *                     <p>
     *                     形如：assume.assume1->assume.assume3 { x >= 0, sid[0]!, 1 }
     *                     </p>
     */
    private static ActionGuard parserTransition(String transLineStr) {
        int leftIdx = transLineStr.indexOf("{");
        if (leftIdx == -1){
            return null;
        }
        transLineStr = transLineStr.substring(leftIdx + 1);
        String[] splitStrs = transLineStr.split(",");
        String timeStr = splitStrs[0].trim();
        String symbolStr = splitStrs[1].trim();
        symbolStr = symbolStr.substring(0, symbolStr.length()-1);
        return new ActionGuard(symbolStr, parserTimeGuard(timeStr));
    }

    /**
     * 把一个timeGuard 字符串转化为TimeGuard实体
     * @param timeGuardStr 表示timeGuard的时间字符串如 x >= or <= or > or < 3 或者形如 x \in [3, 4)
     */
    private static TimeGuard parserTimeGuard(String timeGuardStr) {
        timeGuardStr = timeGuardStr.replace(" ", "");
        TimeGuard timeGuard = new TimeGuard();
        int numberIdx = 2;
        if (timeGuardStr.charAt(2) == '=') {
            numberIdx = 3;
        }
        switch (timeGuardStr.charAt(1)) {
            case '>':
                timeGuard.setLowerBoundOpen(timeGuardStr.charAt(2)!='=');
                timeGuard.setLowerBound(timeGuardStr.charAt(numberIdx) - '0');
                timeGuard.setUpperBoundOpen(true);
                timeGuard.setUpperBound(TimeGuard.MAX_TIME);
                break;
            case '<':
                timeGuard.setLowerBoundOpen(false);
                timeGuard.setLowerBound(0);
                timeGuard.setUpperBoundOpen(timeGuardStr.charAt(2)!='=');
                timeGuard.setUpperBound(timeGuardStr.charAt(numberIdx) - '0');
                break;
            default:
                System.out.println("转换TimeGuardStr: " + timeGuardStr + "\n遇到未知符号 " + timeGuardStr.charAt(1));
        }

        return timeGuard;
    }

}
