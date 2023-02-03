package learn.observationTable.minimal;

import learn.classificationtree.ResetAnswer;
import learn.defaultteacher.BooleanAnswer;
import learn.frame.Teacher;
import lombok.Getter;
import ta.Clock;
import ta.TaLocation;
import ta.TaTransition;
import ta.TimeGuard;
import ta.ota.*;
import ta.timedword.TimeWordHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MinimalObservationTable extends AbstractObservationTable {
    private InclusionRecorder recorder = new InclusionRecorder();

    public MinimalObservationTable(String name, Set<String> sigma, Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher) {
        super(name, sigma, teacher);
    }

    @Override
    public void init(Set<String> sigma) {
        this.sigma = sigma;
        init();
    }

    @Override
    public DOTA buildHypothesis() {
        Clock clock = new Clock("c1");
        List<TaTransition> transitionList = new ArrayList<>();
        //创建观察表每一行和location的映射关系
        Map<Row, TaLocation> rowLocationMap = new HashMap<>();
        // 存储生成的location
        List<TaLocation> locationList = new ArrayList<>();
        //根据s中的row来创建Location
        Set<Row> sRows = new HashSet<>(getRowsBySet(s));
        List<Row> rRows = getRowsByList(r);
        List<Row> rows = new ArrayList<>(sRows);
        // 将recorder中的记录映射到s区
        for (Row key : recorder.childFatherMap.keySet()) {
            Row fatherRow = recorder.childFatherMap.get(key);
            for (Row sRow : sRows) {
                if (sRow.include(fatherRow)) {
                    recorder.childFatherMap.put(key, sRow);
                    break;
                }
            }
        }

        // ----------------------------------------------
//        for (Row rRow : rRows) {
//            int fatherCount = 0;
//            for (Row sRow : sRows) {
//                if (sRow.include(rRow)) fatherCount++;
//            }
//            if (fatherCount >= 2) {
//                System.out.println("-----------------------找到大于两个fatherCount的例子");
//            }
//        }
        // ----------------------------------------------

        rows.addAll(rRows);
        AtomicInteger autoId = new AtomicInteger(0);
        for (int i = 0; i < rows.size(); i++) {
            if (!rowLocationMap.containsKey(rows.get(i))) {
                generateLocation(rowLocationMap, locationList, rows, sRows, i, autoId);
            }
        }
        recorder.rowLocationMap = rowLocationMap;

        //根据观察表来创建Transition
        Set<ResetLogicTimeWord> sr = getPrefixSet();
        for (ResetLogicTimeWord word : sr) {
            if (word.isEmpty()) {
                continue;
            }
            ResetLogicTimeWord pre = word.subWord(0, word.size() - 1);
            if (sr.contains(pre)) {
                ResetLogicAction action = word.getLastResetAction();
                TaLocation sourceLocation = rowLocationMap.get(row(pre));
                TaLocation targetLocation = rowLocationMap.get(row(word));
                String symbol = action.getSymbol();
                TimeGuard timeGuard = TimeGuard.bottomGuard(word.getLastLogicAction());
                Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
                clockTimeGuardMap.put(clock, timeGuard);
                Set<Clock> resetClockSet = new HashSet<>();
                if (action.isReset()) {
                    resetClockSet.add(clock);
                }
                TaTransition transition = new TaTransition(
                        sourceLocation, targetLocation, symbol, clockTimeGuardMap, resetClockSet);
                if (!transitionList.contains(transition)) {
                    transitionList.add(transition);
                }
            }
        }


        DOTA evidenceDOTA = new DOTA(name, sigma, locationList, transitionList, clock);
        evidenceToDOTA(evidenceDOTA);
        this.hypothesis = evidenceDOTA;
        return hypothesis;
    }

    @Override
    public void show() {
        List<String> stringList = new ArrayList<>();
        List<String> suffixStringList = new ArrayList<>();
        List<ResetLogicTimeWord> prefixList = getPrefixList();
        int maxLen = 0;
        for (ResetLogicTimeWord word : prefixList) {
            String s = word.toString();
            stringList.add(s);
            maxLen = Math.max(maxLen, s.length());
        }
        for (LogicTimeWord words : suffixSet) {
            String s = words.toString();
            suffixStringList.add(s);
        }


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < maxLen; i++) {
            sb.append(" ");
        }
        sb.append("|");
        for (String s : suffixStringList) {
            sb.append(s);
            sb.append("|");
        }
        sb.append("\n");

        for (int i = 0; i < prefixList.size(); i++) {
            String prefixString = stringList.get(i);
            sb.append(prefixString);
            int slen = s.size();
            for (int k = 0; k < maxLen - prefixString.length(); k++) {
                sb.append(" ");
            }
            sb.append("|");
            for (int j = 0; j < suffixSet.size(); j++) {
                Pair pair = new Pair(prefixList.get(i), suffixSet.get(j));
                ResetAnswer answer = answers.get(pair);
                sb.append(answer.toString());
                String suffixString = suffixStringList.get(j);
                for (int k = 0; k < suffixString.length() - answer.size(); k++) {
                    sb.append(" ");
                }
                sb.append("|");
            }
            TaLocation location = recorder.rowLocationMap.get(row(prefixList.get(i)));
            if (location != null) {
                sb.append("[").append(location.getName()).append(" ").append(location.isAccept() ? "+" : "-").append("]");
                sb.append("    |");
            }
            Row fatherRow = recorder.childFatherMap.get(row(prefixList.get(i)));
            if (fatherRow != null) {
                sb.append(recorder.childFatherMap.get(row(prefixList.get(i))).getPrefix());
            }

            sb.append("\n");

            if (i == slen - 1) {
                for (int k = 0; k < maxLen; k++) {
                    sb.append("-");
                }
                sb.append("|");
                for (String suffixString : suffixStringList) {
                    for (int k = 0; k < suffixString.length(); k++) {
                        sb.append("-");
                    }
                    sb.append("|");
                }
                sb.append("\n");
            }
        }
        System.out.println(sb);
    }

    /**
     * 把row生成对应的location，如果生成成功就把row加入到rowSet里
     */
    private TaLocation generateLocation(Map<Row, TaLocation> rowLocationMap, List<TaLocation> locationList, List<Row> rows, Set<Row> sRows, int sIndex, AtomicInteger locationId) {
        Row row = rows.get(sIndex);
        ResetLogicTimeWord sWord = row.getPrefix();
        TaLocation location = null;
        if (sRows.contains(row)) {
            int curId = locationId.getAndIncrement();
            boolean init = row.equals(row(ResetLogicTimeWord.emptyWord()));
            Pair pair = new Pair(sWord, LogicTimeWord.emptyWord());
            boolean accepted = answers.get(pair).isAccept();
            location = new TaLocation(String.valueOf(curId), String.valueOf(curId), init, accepted);
            locationList.add(location);
        } else {
            // 寻找s取可以代表r取的row
            if (recorder.childFatherMap.containsKey(row)) {
                location = rowLocationMap.get(recorder.childFatherMap.get(row));
            } else {
                for (int i = 0; i < rows.size(); i++) {
                    Row fatherRow = rows.get(i);
                    if (i >= sRows.size()) {
                        break;
                    }
                    if (!fatherRow.include(row)) {
                        continue;
                    }
                    location = rowLocationMap.get(fatherRow);
                    break;
                }
            }

        }
        if (location == null) {
            show();
            throw new RuntimeException("find invalid location while building locations:\n"
                    + row.getPrefix() + "\n"
                    + row.getAnswers() + "\n");
        }
        rowLocationMap.put(row, location);
        return location;
    }

    /**
     * 当e区新加后缀时调用，检查是否存在:
     * s区带有无效值，但是r区存在没有无效值但是可以模拟s区的情况
     */
    private void checkInclusion() {
        List<Row> sRowSet = getRowsByList(s);
        List<Row> rRowSet = getRowsByList(r);

        for (Row sRow : sRowSet) {
            ResetAnswer lastAnswer = sRow.getLastAnswer();
            if (lastAnswer.isHasInvalid()) {
                Row targetRow = null;
                for (Row rRow : rRowSet) {
                    if (!rRow.getLastAnswer().isHasInvalid() && rRow.include(sRow)) {
                        if (targetRow == null
                                || (rRow.getPrefix().size() < targetRow.getPrefix().size()))
                            targetRow = rRow;
                    }
                }
                if (targetRow != null) {
                    downS(sRow);
                    makeClosed(targetRow);
                }
            }
        }
    }

    protected Row isClosed() {
        List<Row> sRowSet = getRowsByList(s);
        List<Row> rRowSet = getRowsByList(r);
        sRowSet.sort(Comparator.comparingInt(Row::getInvalidCount));
        rRowSet.sort(Comparator.comparingInt(Row::getInvalidCount));
        for (int i = 0; i < sRowSet.size(); i++) {
            for (int j = i + 1; j < sRowSet.size(); j++) {
                Row row1 = sRowSet.get(i);
                Row row2 = sRowSet.get(j);
                if (row2.isInvalidAnswer() && row1.include(row2)) {
                    downS(row2);
                }
            }
        }
        f1:
        for (Row rRow : rRowSet) {
            for (Row sRow : sRowSet) {
                if (sRow.include(rRow)) {
                    continue f1;
                }
            }
            return rRow;
        }
        return null;
    }

    protected void makeClosed(Row row) {
        ResetLogicTimeWord word = row.getPrefix();
        s.add(word);
        r.remove(word);
        for (String action : sigma) {
            LogicTimedAction logicAction = new LogicTimedAction(action, 0d);
            LogicTimeWord logicTimeWord = word.logicTimeWord().concat(logicAction);
            ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
            // 无效的前缀不蕴涵任何信息不加入观察表
            if (TimeWordHelper.isValidWord(resetWord) && !s.contains(resetWord)) {
                r.add(resetWord);
            }
        }
        fillTable(r);
    }

    /**
     * 把一行row从S区降到R区
     */
    private void downS(Row row) {
        s.remove(row.getPrefix());
        List<ResetLogicTimeWord> removeWord = new ArrayList<>();
        for (String element : sigma) {
            removeWord.add(row.getPrefix().concat(new ResetLogicAction(element, 0.0d, true)));
        }
        removeWord.forEach(r::remove);
        r.add(row.getPrefix());
    }

    private Set<Row> getRowsBySet(Collection<ResetLogicTimeWord> set) {
        Set<Row> rows = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            for (ResetLogicTimeWord word : set) {
                rows.add(row(word));
            }
        }
        return rows;
    }

    private List<Row> getRowsByList(Collection<ResetLogicTimeWord> set) {
        return set.stream().map(this::row).collect(Collectors.toList());
    }

    /**
     * 获得观察表的一行
     */
    private Row row(ResetLogicTimeWord resetLogicTimeWord) {
        if (!s.contains(resetLogicTimeWord) && !r.contains(resetLogicTimeWord)) {
            return null;
        }
        return wideRow(resetLogicTimeWord);
    }

    /**
     * 获得一个前缀的对应拼接后缀分别得到的结果
     */
    private Row wideRow(ResetLogicTimeWord resetLogicTimeWord) {
        Row row = new Row(resetLogicTimeWord);
        for (LogicTimeWord suffixWord : suffixSet) {
            Pair pair = new Pair(resetLogicTimeWord, suffixWord);
            ResetAnswer answer = answers.get(pair);
            row.add(answer);
        }
        return row;
    }

    protected ResetLogicTimeWord isEvidenceClosed() {
        for (ResetLogicTimeWord sPrefix : s) {
            for (LogicTimeWord suffix : suffixSet) {
                LogicTimeWord logicTimeWord = sPrefix.logicTimeWord().concat(suffix);
                ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
                if (!s.contains(resetWord) && !r.contains(resetWord) && TimeWordHelper.isValidWord(resetWord)) {
                    return resetWord;
                }
            }
        }
        return null;
    }

    protected void makeEvidenceClosed(ResetLogicTimeWord keyWord) {
        r.add(keyWord);
        fillTable(r);
    }

    protected ConsistentResult isConsistent() {
        long start = System.currentTimeMillis();
        List<Row> sRows = getRowsByList(s);
        List<Row> rRows = getRowsByList(r);
        List<Row> r1Rows = new ArrayList<>();
        List<Row> r2Rows = new ArrayList<>();
        recorder = new InclusionRecorder();
        rRows.forEach(rRow -> {
            // 如果rRow是纯有效的，直接放到r1区即可
            if (!rRow.isInvalidAnswer()) {
                r1Rows.add(rRow);
                return;
            }

            // 如果rRow有无效段, 需要统计其可以被s区多少个row代替，放在r2区
            int fatherCount = 0;
            for (Row sRow : sRows) {
                if (sRow.include(rRow)) fatherCount++;
            }
            rRow.setFatherCount(fatherCount);
            r2Rows.add(rRow);
        });
        if (r2Rows.size() != 0) {
            r2Rows.sort(Comparator.comparingInt(Row::getFatherCount));
        }
        rRows.clear();
        rRows.addAll(r1Rows);
        rRows.addAll(r2Rows);
        for (Row rRow : rRows) {
            for (Row sRow : sRows) {
                if (sRow.include(rRow)) {
//                    if (rrow.getPrefix().equals(ResetLogicTimeWord.emptyWord().concat(new ResetLogicAction("a", 10.0, true)))) {
//                        String w = "";
//                    }
                    InclusionRecorder cache = recorder.clone();
                    boolean relationCheck = recorder.addRelationSmartly(rRow, sRow);
                    if (!relationCheck) continue;
                    ConsistentResult result = null;
                    //todo 修改为一致性校验
                    try {
                        result = checkSimulation(sRow, rRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                    // todo 互模拟校验
                    try {
                        result = checkSimulation(rRow, sRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        consistentTime += end - start;
        boolean loopCheck = false;
        for (Row sRow : sRows) {
            // 互模拟校验过程中可能出现信息更全的一行 如："minimal\\4_4_20\\4_4_20-7.json", 此时需要把信息更全的一行加入到观察表
            Row strongRow = recorder.childFatherMap.get(sRow);
            if (strongRow != null && !r.contains(strongRow.getPrefix())) {
                r.add(strongRow.getPrefix());
                loopCheck = true;
            }
        }
        if (loopCheck) return new ConsistentResult(null, null, null);

        return null;
    }

    protected void makeConsistent(ConsistentResult consistentResult) {
        ResetLogicTimeWord word1 = consistentResult.getFatherRowWord();
        ResetLogicTimeWord word2 = consistentResult.getChildRowWord();
        LogicTimedAction action = consistentResult.getKeyAction();
        if (word1 == null && word2 == null) {
            if (action == null) return;
            suffixSet.add(LogicTimeWord.emptyWord().concat(action));
        } else {
            for (LogicTimeWord w : suffixSet) {
                Pair pair1 = new Pair(word1, w);
                Pair pair2 = new Pair(word2, w);

                ResetAnswer answer1 = answers.get(pair1);
                ResetAnswer answer2 = answers.get(pair2);
                if (!answer1.include(answer2)) {
                    LogicTimeWord word = consistentWord(action, w);
                    suffixSet.add(word);
                    break;
                }
            }
        }
        fillTable();
        checkInclusion();
    }

    private LogicTimeWord consistentWord(LogicTimedAction logicTimedAction, LogicTimeWord logicTimeWord) {
        List<LogicTimedAction> logicTimedActions = new ArrayList<>();
        logicTimedActions.add(logicTimedAction);
        logicTimedActions.addAll(logicTimeWord.getTimedActions());
        return new LogicTimeWord(logicTimedActions);
    }

    private Set<LogicTimedAction> getLastActionSet() {
        Set<ResetLogicTimeWord> sr = getPrefixSet();
        Set<LogicTimedAction> lastActionSet = new HashSet<>();
        for (ResetLogicTimeWord resetWord : sr) {
            if (!resetWord.isEmpty()) {
                LogicTimedAction last = resetWord.getLastLogicAction();
                lastActionSet.add(last);
            }
        }
        return lastActionSet;
    }

    protected ResetAnswer answer(ResetLogicTimeWord prefix, LogicTimeWord suffix) {
        LogicTimeWord word = prefix.logicTimeWord().concat(suffix);
        ResetLogicTimeWord resetWord = teacher.transferWord(word);
        BooleanAnswer answer = teacher.membership(resetWord);
        List<Boolean> resetList = new ArrayList<>();
        int len1 = prefix.size();
        int len2 = word.size();
        for (int i = len1; i < len2; i++) {
            ResetLogicAction resetLogicAction = resetWord.get(i);
            if (resetLogicAction.isInvalid()) {
                resetList.add(null);
                break;
            } else {
                resetList.add(resetLogicAction.isReset());
            }
        }
        if (len1 == len2 && answer.isInvalid()) {
            resetList.add(null);
        }
        return new ResetAnswer(resetList, answer.isAccept());
    }

    /**
     * 检验 row1 是否可以模拟 row2
     */
    private ConsistentResult checkSimulation(Row row1, Row row2) throws Exception {
        Map<LogicTimedAction, Row> row1NextAction = getNextAction(row1);
        for (Map.Entry<LogicTimedAction, Row> entry : row1NextAction.entrySet()) {
            LogicTimedAction lastAction = entry.getKey();
            Row row1NextRow = entry.getValue();
            ResetLogicTimeWord row2NextWord = teacher.transferWord(row2.getPrefix().logicTimeWord().concat(lastAction));
            if (!TimeWordHelper.isValidWord(row2NextWord)) {
                // 如果拼接出来的逻辑时间字是一个无效的逻辑时间字则不需要后续的校验
                continue;
            }
            // row1和row2接相同的后缀重置信息不一样则lastAction就是可区分他们的后缀
            if (row1NextRow.getPrefix().getLastResetAction().isReset() != row2NextWord.getLastResetAction().isReset()) {
                return new ConsistentResult(null, null, lastAction);
            }
            Set<ResetLogicTimeWord> prefixes = getPrefixSet();
            if (!prefixes.contains(row2NextWord)) {
                fillTable(row2NextWord);
            }
            Row row2NextRow = wideRow(row2NextWord);
            if (row1NextRow.include(row2NextRow)) {
                boolean result = recorder.addRelationSmartly(row2NextRow, row1NextRow);
                if (!result) {
                    throw new Exception("relation bind fail!");
                }
            } else if (row2NextRow.include(row1NextRow)) {
                boolean result = recorder.addRelationSmartly(row1NextRow, row2NextRow);
                if (!result) {
                    throw new Exception("relation bind fail!");
                }
            } else {
                return new ConsistentResult(row1NextRow.getPrefix(), row2NextWord, lastAction);
            }
        }
        return null;
    }

    private Map<LogicTimedAction, Row> getNextAction(Row row) {
        List<ResetLogicTimeWord> prefixes = getPrefixList();
        Map<LogicTimedAction, Row> map = new HashMap<>();
        // 提取row1 后继的action和 接上action后对应的新row
        for (ResetLogicTimeWord p : prefixes) {
            if (p.size() != row.getPrefix().size() + 1) continue;

            if (p.subWord(0, row.getPrefix().size()).equals(row.getPrefix())) {
                map.put(p.getLastResetAction().logicTimedAction(), row(p));
            }
        }
        return map;
    }

    /**
     * 模拟关系记录类
     */
    private static class InclusionRecorder implements Cloneable {
        @Getter
        private Map<Row, Row> childFatherMap;
        @Getter
        private Map<Row, List<Row>> fatherChildMap;
        @Getter
        private Map<Row, TaLocation> rowLocationMap;

        // 记录包含映射关系的map, key是 child row, val是 father row.
        public InclusionRecorder() {
            childFatherMap = new HashMap<>();
            fatherChildMap = new HashMap<>();
            rowLocationMap = new HashMap<>();
        }

        @Override
        public InclusionRecorder clone() {
            InclusionRecorder o = new InclusionRecorder();
            o.childFatherMap = new HashMap<>(childFatherMap);
            o.fatherChildMap = new HashMap<>(fatherChildMap);
            o.rowLocationMap = new HashMap<>(rowLocationMap);
            return o;
        }

        /**
         * 添加一条父子关系, 如果child的父亲节点不是部分无效节点且和即将绑定的父亲节点不能互相包含时返回false.
         */
        public boolean addRelationSmartly(Row child, Row father) {
            if (father.equals(child)) return false;
            while (childFatherMap.containsKey(father)) {
                father = childFatherMap.get(father);
            }
            if (childFatherMap.containsKey(child)) {
                Row rowFather = childFatherMap.get(child);
                if (!rowFather.isInvalidAnswer() && !father.isInvalidAnswer()) {
                    if (!rowFather.include(father)) {
                        // error
                        String pause = "pause";
                        return false;
                    }
                    return true;
                } else if (rowFather.include(father)) {
                    addRelation(father, rowFather);
                    addRelation(child, rowFather);
                    return true;
                } else if (father.include(rowFather)) {
                    moveFatherRelation(rowFather, father);
                    addRelation(rowFather, father);
                    addRelation(child, father);
                    return true;
                } else {
                    return false;
                }
            }
            // 不记录纯有效的child father
            if (!child.isInvalidAnswer() && !father.isInvalidAnswer()) {
                return true;
            }
            addRelation(child, father);
            return true;
        }

        /**
         * 发现了father更高的父级，更改father的所有孩子到更高的父级
         */
        private void moveFatherRelation(Row subFather, Row father) {
            List<Row> childOfSubFather = fatherChildMap.get(subFather);
            if (childOfSubFather == null) return;
            // 更新fatherChildMap
            fatherChildMap.remove(subFather);
            // 更新childFatherMap
            childOfSubFather.forEach(child -> addRelation(child, father));
        }

        /**
         * 添加一条父子级信息
         */
        public void addRelation(Row child, Row father) {
            if (father.equals(child)) return;
            // 更新childFatherMap和fatherChildMap
            childFatherMap.put(child, father);
            List<Row> children = fatherChildMap.getOrDefault(father, new ArrayList<>());
            children.add(child);
            fatherChildMap.put(father, children);

            // 如果child也有child, 则要把child的child都设为父级为
            moveFatherRelation(child, father);
        }
    }


}
