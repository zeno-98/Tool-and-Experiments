package learn.observationTable;

import learn.classificationtree.ResetAnswer;
import learn.defaultteacher.BooleanAnswer;
import learn.frame.Learner;
import learn.frame.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

@Data
public class NewObserbationTable implements Learner<ResetLogicTimeWord> {
    private String name;
    private Set<String> sigma;
    private Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher;
    private Set<ResetLogicTimeWord> s;
    private Set<ResetLogicTimeWord> r;
    private List<LogicTimeWord> suffixSet;
    private Map<Pair, ResetAnswer> answers;
    private DOTA hypothesis;
    private boolean includedCheckMode;
    private boolean biSimulationCheck;
    private InclusionRecorder recorder = new InclusionRecorder();

    public NewObserbationTable(String name, Set<String> sigma, Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher) {
        this.name = name;
        this.sigma = sigma;
        this.teacher = teacher;
    }

    /**
     * true表示使用基于包含关系的观察表
     */
    public void setIncludedCheck(boolean open) {
        this.includedCheckMode = open;
    }

    public void setBiSimulationCheck(boolean open) {
        this.biSimulationCheck = open;
    }

    @Override
    public void init() {
        s = new HashSet<>();
        r = new HashSet<>();
        suffixSet = new ArrayList<>();
        s.add(ResetLogicTimeWord.emptyWord());
        suffixSet.add(LogicTimeWord.emptyWord());

        for (String symbol : sigma) {
            LogicTimedAction logicAction = new LogicTimedAction(symbol, 0d);
            LogicTimeWord timeWord = new LogicTimeWord(Collections.singletonList(logicAction));
            ResetLogicTimeWord resetLogicTimeWord = teacher.transferWord(timeWord);
            r.add(resetLogicTimeWord);
        }
        answers = new HashMap<>();
    }

    @Override
    public void init(Set<String> sigma) {
        this.sigma = sigma;
        init();
    }

    @Override
    public void learn() {
        fillTable();
        while (true) {
            Row closeRow = isClosed();
            if (closeRow != null) {
                makeClosed(closeRow);
                continue;
            }

            ResetLogicTimeWord keyWord = isEvidenceClosed();
            if (keyWord != null) {
                makeEvidClosed(keyWord);
                continue;
            }

            ConsistentResult consistentResult;
            if (biSimulationCheck) {
                consistentResult = isBiSimulation();
            } else {
                consistentResult = isConsistent();
            }
            if (consistentResult != null) {
                makeConsistent(consistentResult);
                continue;
            }
            break;
        }
    }

    @Override
    public void refine(ResetLogicTimeWord counterExample) {
        Set<ResetLogicTimeWord> prefixesSet = counterExample.getAllPrefixes();
        prefixesSet.removeAll(s);
        r.addAll(prefixesSet);
        learn();
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
        if (includedCheckMode) {
            r.removeIf(resetLogicTimeWord -> !recorder.childFatherMap.containsKey(row(resetLogicTimeWord)));
        }
        List<Row> rows = new ArrayList<>(sRows);
        rows.addAll(getRowsByList(r));
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

    @Override
    public DOTA getFinalHypothesis() {
        return hypothesis;
    }

    @Override
    public boolean check(ResetLogicTimeWord counterExample) {
        return false;
    }

    /**
     * 把row生成对应的location，如果生成成功就把row加入到rowSet里
     */
    private TaLocation generateLocation(Map<Row, TaLocation> rowLocationMap, List<TaLocation> locationList, List<Row> rows, Set<Row> sRows, int sIndex, AtomicInteger locationId) {
        Row row = rows.get(sIndex);
        ResetLogicTimeWord sWord = row.prefix;
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
                    + row.prefix + "\n"
                    + row.answers + "\n");
        }
        rowLocationMap.put(row, location);
        return location;
    }

    private void fillTable() {
        fillTable(s);
        fillTable(r);
    }

    private void fillTable(Set<ResetLogicTimeWord> set) {
        for (ResetLogicTimeWord prefix : set) {
            fillTable(prefix);
        }
    }

    private void fillTable(ResetLogicTimeWord prefix) {
        for (LogicTimeWord suffix : suffixSet) {
            Pair pair = new Pair(prefix, suffix);
            if (!answers.containsKey(pair)) {
                ResetAnswer answer = answer(prefix, suffix);
                answers.put(pair, answer);
            }
        }
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

    private Row isClosed() {
        List<Row> sRowSet = getRowsByList(s);
        List<Row> rRowSet = getRowsByList(r);
        sRowSet.sort(Comparator.comparingInt(a -> a.invalidCount));
        rRowSet.sort(Comparator.comparingInt(a -> a.invalidCount));
        for (int i = 0; i < sRowSet.size(); i++) {
            for (int j = i + 1; j < sRowSet.size(); j++) {
                Row row1 = sRowSet.get(i);
                Row row2 = sRowSet.get(j);
                if (row2.hasInvalidAnswer && row1.include(row2)) {
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

    private void makeClosed(Row row) {
        ResetLogicTimeWord word = row.getPrefix();
        s.add(word);
        r.remove(word);
        for (String action : sigma) {
            LogicTimedAction logicAction = new LogicTimedAction(action, 0d);
            LogicTimeWord logicTimeWord = word.logicTimeWord().concat(logicAction);
            ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
            if (!s.contains(resetWord)) {
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

    public ResetLogicTimeWord isEvidenceClosed() {
        for (ResetLogicTimeWord sPrefix : s) {
            for (LogicTimeWord suffix : suffixSet) {
                LogicTimeWord logicTimeWord = sPrefix.logicTimeWord().concat(suffix);
                ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
                if (!s.contains(resetWord) && !r.contains(resetWord)) {
                    return resetWord;
                }
            }
        }
        return null;
    }

    public void makeEvidClosed(ResetLogicTimeWord keyWord) {
        r.add(keyWord);
        fillTable(r);
    }

    private ConsistentResult isConsistent() {
        recorder = new InclusionRecorder();
        Set<LogicTimedAction> logicActionSet = getLastActionSet();
        Set<ResetLogicTimeWord> prefixSet = getPrefixSet();
        List<Row> sRows = getRowsByList(s);
        List<Row> rRows = getRowsByList(r);
        List<Row> r1Rows = new ArrayList<>();
        List<Row> r2Rows = new ArrayList<>();
        rRows.forEach(rRow -> {
            // 如果rRow是纯有效的，直接放到r1区即可
            if (!rRow.hasInvalidAnswer) {
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
            r2Rows.sort(Comparator.comparingInt(a -> a.fatherCount));
        }
        // entityRows表示所有没有无效段的row
        List<Row> entityRows = new ArrayList<>(sRows);
        entityRows.addAll(r1Rows);
        // entityRows之间的搜索
        for (int i = 0; i < entityRows.size(); i++) {
            Row row1 = entityRows.get(i);
            for (int j = i + 1; j < entityRows.size(); j++) {
                Row row2 = entityRows.get(j);
                if (!row1.include(row2)) continue;
                // 如果row1,row2都有效,则进行后缀一致性判断

                // 如果row1是有效的，row2是部分无效的，如果row2的father和row1一样则进行判断

                // 如果row1是部分无效的，row2也是部分，如果他们的father是一样的则进行一致性判断
                for (LogicTimedAction action : logicActionSet) {
                    LogicTimeWord word1 = row1.getPrefix().logicTimeWord().concat(action);
                    LogicTimeWord word2 = row2.getPrefix().logicTimeWord().concat(action);
                    ResetLogicTimeWord resetWord1 = teacher.transferWord(word1);
                    ResetLogicTimeWord resetWord2 = teacher.transferWord(word2);

                    if (!prefixSet.contains(resetWord1) || !prefixSet.contains(resetWord2)) {
                        continue;
                    }
                    Row newRow1 = row(resetWord1);
                    Row newRow2 = row(resetWord2);
                    if (newRow1 == null || newRow2 == null) {
                        continue;
                    }
                    if (!newRow1.include(newRow2)) {
                        return new ConsistentResult(resetWord1, resetWord2, action);
                    }
                    if (resetWord1.getLastResetAction().isReset() != resetWord2.getLastResetAction().isReset()) {
                        String a = "";
                    }
                    if (newRow2.hasInvalidAnswer) {
                        boolean result = recorder.addRelationSmartly(newRow2, newRow1);
                        if (!result) {
                            String a = "a";
                        }
                    }
                }
            }
        }

        // entityRow 和 r2之间的搜索
        for (Row row2 : r2Rows) {
            for (Row row1 : entityRows) {
                if (row1.equals(row2)) continue;
                if (!row1.include(row2)) continue;
                if (!recorder.addRelationSmartly(row2, row1)) continue;

                for (LogicTimedAction action : logicActionSet) {
                    LogicTimeWord word1 = row1.getPrefix().logicTimeWord().concat(action);
                    LogicTimeWord word2 = row2.getPrefix().logicTimeWord().concat(action);
                    ResetLogicTimeWord resetWord1 = teacher.transferWord(word1);
                    ResetLogicTimeWord resetWord2 = teacher.transferWord(word2);

                    if (!prefixSet.contains(resetWord1) || !prefixSet.contains(resetWord2)) {
                        continue;
                    }
                    Row newRow1 = row(resetWord1);
                    Row newRow2 = row(resetWord2);
                    if (newRow1 == null || newRow2 == null) {
                        continue;
                    }
                    if (!newRow1.include(newRow2)) {
                        return new ConsistentResult(resetWord1, resetWord2, action);
                    }
                    if (newRow2.hasInvalidAnswer) {
                        boolean result = recorder.addRelationSmartly(newRow2, newRow1);
                        if (!result) {
                            String a = "a";
                        }
                    }
                }
            }
        }

        for (Row row1 : r2Rows) {
            for (Row row2 : r2Rows) {
                if (!row1.include(row2)) continue;
                if (!recorder.addRelationSmartly(row2, row1)) continue;

                for (LogicTimedAction action : logicActionSet) {
                    LogicTimeWord word1 = row1.getPrefix().logicTimeWord().concat(action);
                    LogicTimeWord word2 = row2.getPrefix().logicTimeWord().concat(action);
                    ResetLogicTimeWord resetWord1 = teacher.transferWord(word1);
                    ResetLogicTimeWord resetWord2 = teacher.transferWord(word2);

                    if (!prefixSet.contains(resetWord1) || !prefixSet.contains(resetWord2)) {
                        continue;
                    }
                    Row newRow1 = row(resetWord1);
                    Row newRow2 = row(resetWord2);
                    if (newRow1 == null || newRow2 == null) {
                        continue;
                    }
                    if (!newRow1.include(newRow2)) {
                        return new ConsistentResult(resetWord1, resetWord2, action);
                    }
                    if (newRow2.hasInvalidAnswer) {
                        boolean result = recorder.addRelationSmartly(newRow2, newRow1);
                        if (!result) {
                            String a = "a";
                        }
                    }
                }
            }
        }
        return null;
    }

    private ConsistentResult isBiSimulation() {
        List<Row> sRows = getRowsByList(s);
        List<Row> rRows = getRowsByList(r);
        List<Row> r1Rows = new ArrayList<>();
        List<Row> r2Rows = new ArrayList<>();
        recorder = new InclusionRecorder();
        rRows.forEach(rRow -> {
            // 如果rRow是纯有效的，直接放到r1区即可
            if (!rRow.hasInvalidAnswer) {
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
            r2Rows.sort(Comparator.comparingInt(a -> a.fatherCount));
        }
        rRows.clear();
        rRows.addAll(r1Rows);
        rRows.addAll(r2Rows);
        for (Row rRow : rRows) {
            for (Row sRow : sRows) {
                if (sRow.include(rRow)) {
//                    if (rRow.prefix.equals(ResetLogicTimeWord.emptyWord().concat(new ResetLogicAction("a", 10.0, true)))) {
//                        String w = "";
//                    }
                    InclusionRecorder cache = recorder.clone();
                    boolean relationCheck = recorder.addRelationSmartly(rRow, sRow);
                    if (!relationCheck) continue;
                    ConsistentResult result;
                    try {
                        result = checkSimulation(sRow, rRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                    try {
                        result = checkSimulation(rRow, sRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                }
            }
        }

        for (Row rRow : rRows) {
            for (Row sRow : r1Rows) {
                if (sRow != rRow && sRow.include(rRow)) {
//                    if (rRow.prefix.equals(ResetLogicTimeWord.emptyWord().concat(new ResetLogicAction("a", 10.0, true)))) {
//                        String w = "";
//                    }
                    InclusionRecorder cache = recorder.clone();
                    boolean relationCheck = recorder.addRelationSmartly(rRow, sRow);
                    if (!relationCheck) continue;
                    ConsistentResult result;
                    try {
                        result = checkSimulation(sRow, rRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                    try {
                        result = checkSimulation(rRow, sRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                }
            }
        }

        for (Row rRow : r2Rows) {
            for (Row sRow : r2Rows) {
                if (sRow != rRow && sRow.include(rRow)) {
                    InclusionRecorder cache = recorder.clone();
                    boolean relationCheck = recorder.addRelationSmartly(rRow, sRow);
                    if (!relationCheck) continue;
                    ConsistentResult result = null;
                    try {
                        result = checkSimulation(sRow, rRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                    try {
                        result = checkSimulation(rRow, sRow);
                    } catch (Exception e) {
                        recorder = cache;
                        continue;
                    }
                    if (result != null) return result;
                }
            }
        }
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

    private void makeConsistent(ConsistentResult consistentResult) {
        ResetLogicTimeWord word1 = consistentResult.fatherRowWord;
        ResetLogicTimeWord word2 = consistentResult.childRowWord;
        LogicTimedAction action = consistentResult.getKeyAction();
        if (word1 == null || word2 == null || action == null) return;

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
        fillTable();
        if (includedCheckMode) {
            checkInclusion();
        }
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

    private Set<ResetLogicTimeWord> getPrefixSet() {
        Set<ResetLogicTimeWord> sr = new HashSet<>();
        sr.addAll(s);
        sr.addAll(r);
        return sr;
    }

    private List<ResetLogicTimeWord> getPrefixList() {
        List<ResetLogicTimeWord> logicTimeWordList = new ArrayList<>();
        logicTimeWordList.addAll(s);
        logicTimeWordList.addAll(r);
        return logicTimeWordList;
    }

    private ResetAnswer answer(ResetLogicTimeWord prefix, LogicTimeWord suffix) {
        LogicTimeWord word = prefix.logicTimeWord().concat(suffix);
        ResetLogicTimeWord resetWord = teacher.transferWord(word);
        BooleanAnswer answer = teacher.membership(resetWord);
        List<Boolean> resetList = new ArrayList<>();
        int len1 = prefix.size();
        int len2 = word.size();
        for (int i = len1; i < len2; i++) {
            ResetLogicAction resetLogicAction = resetWord.get(i);
            if (includedCheckMode && resetLogicAction.isInvalid()) {
                resetList.add(null);
                break;
            } else {
                resetList.add(resetLogicAction.isReset());
            }
        }
        if (includedCheckMode && len1 == len2 && answer.isInvalid()) {
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
                Row tmp = childFatherMap.get(father);
                father = tmp;
            }
            if (childFatherMap.containsKey(child)) {
                Row rowFather = childFatherMap.get(child);
                if (!rowFather.hasInvalidAnswer && !father.hasInvalidAnswer) {
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

    @Data
    @AllArgsConstructor
    private static class ConsistentResult {
        private ResetLogicTimeWord fatherRowWord;
        private ResetLogicTimeWord childRowWord;
        private LogicTimedAction keyAction;
    }

    @Data
    @AllArgsConstructor
    private static class Pair {
        private ResetLogicTimeWord prefix;
        private LogicTimeWord suffix;

        public LogicTimeWord timeWord() {
            LogicTimeWord pre = prefix.logicTimeWord();
            return pre.concat(suffix);
        }
    }

    @Data
    private class Row {
        private List<ResetAnswer> answers;
        private boolean hasInvalidAnswer;
        private ResetLogicTimeWord prefix;
        private int invalidCount;
        @EqualsAndHashCode.Exclude
        private int fatherCount;

        public Row(ResetLogicTimeWord prefix) {
            this.prefix = prefix;
            answers = new ArrayList<>();
            hasInvalidAnswer = false;
        }

        public boolean include(Row o) {
            if (answers.size() != o.answers.size()) return false;
            for (int i = 0; i < answers.size(); i++) {
                if (!answers.get(i).include(o.get(i))) return false;
            }
            return true;
        }

        public ResetAnswer get(int i) {
            return answers.get(i);
        }

        public ResetAnswer getLastAnswer() {
            return answers.get(answers.size() - 1);
        }

        public void add(ResetAnswer answer) {
            if (answer.isHasInvalid()) {
                invalidCount++;
                hasInvalidAnswer = true;
            }
            answers.add(answer);
        }
    }


}
