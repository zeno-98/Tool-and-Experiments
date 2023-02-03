package learn.observationTable;

import learn.classificationtree.ResetAnswer;
import learn.defaultteacher.BooleanAnswer;
import learn.frame.Learner;
import learn.frame.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import ta.Clock;
import ta.TaLocation;
import ta.TaTransition;
import ta.TimeGuard;
import ta.ota.*;

import java.util.*;

@Data
public class ObservationTable implements Learner<ResetLogicTimeWord> {
    private String name;
    private Set<String> sigma;
    private Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher;
    private Set<ResetLogicTimeWord> s;
    private Set<ResetLogicTimeWord> r;
    private List<LogicTimeWord> suffixSet;
    private Map<Pair, ResetAnswer> answers;
    private DOTA hypothesis;
    private ResetLogicTimeWord keyWord;
    private List<ResetLogicTimeWord> unConsistentCouple = null;
    private LogicTimedAction key = null;

    public ObservationTable(String name, Set<String> sigma, Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher) {
        this.name = name;
        this.sigma = sigma;
        this.teacher = teacher;
    }

    @Override
    public void init(Set<String> sigma) {
        this.sigma = sigma;
        init();
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

    private ResetAnswer answer(ResetLogicTimeWord prefix, LogicTimeWord suffix) {
        LogicTimeWord word = prefix.logicTimeWord().concat(suffix);
        ResetLogicTimeWord resetWord = teacher.transferWord(word);
        BooleanAnswer answer = teacher.membership(resetWord);
        List<Boolean> resetList = new ArrayList<>();
        int len1 = prefix.size();
        int len2 = word.size();
        for (int i = len1; i < len2; i++) {
            ResetLogicAction resetLogicAction = resetWord.get(i);
            resetList.add(resetLogicAction.isReset());
        }
        return new ResetAnswer(resetList, answer.isAccept());
    }

    @Override
    public void learn() {
        fillTable();
        while (!isPrepared()) {
            if (!isClosed()) {
                makeClosed();
            }
            if (!isConsistent()) {
                makeConsistent();
            }
            if (!isEvidenceClosed()) {
                makeEvidClosed();
            }
        }
    }

    @Override
    public void refine(ResetLogicTimeWord counterExample) {
        Set<ResetLogicTimeWord> prefixesSet = counterExample.getAllPrefixes();
        prefixesSet.removeAll(s);
        r.addAll(prefixesSet);
        fillTable(r);
        while (!isPrepared()) {
            if (!isClosed()) {
                makeClosed();
            }
            if (!isConsistent()) {
                makeConsistent();
            }
            if (!isEvidenceClosed()) {
                makeEvidClosed();
            }
        }
    }

    @Override
    public boolean check(ResetLogicTimeWord counterExample) {
        return false;
    }

    @Override
    public DOTA buildHypothesis() {
        Clock clock = new Clock("c1");
        List<TaLocation> locationList = new ArrayList<>();
        List<TaTransition> transitionList = new ArrayList<>();
        //创建观察表每一行和location的映射关系
        Map<Row, TaLocation> rowLocationMap = new HashMap<>();
        //根据s中的row来创建Location
        Set<Row> rowSet = new HashSet<>();
        int id = 1;
        for (ResetLogicTimeWord sWord : s) {
            Row row = row(sWord);
            if (!rowSet.contains(row)) {
                rowSet.add(row);
                boolean init = row(sWord).equals(row(ResetLogicTimeWord.emptyWord()));
                Pair pair = new Pair(sWord, LogicTimeWord.emptyWord());
                boolean accepted = answers.get(pair).isAccept();
                TaLocation location = new TaLocation(String.valueOf(id), String.valueOf(id), init, accepted);
                locationList.add(location);
                rowLocationMap.put(row, location);
                id++;
            }
        }

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

    private void fillTable() {
        fillTable(s);
        fillTable(r);
    }

    private void fillTable(Set<ResetLogicTimeWord> set) {
        for (ResetLogicTimeWord prefix : set) {
            for (LogicTimeWord suffix : suffixSet) {
                Pair pair = new Pair(prefix, suffix);
                if (!answers.containsKey(pair)) {
                    ResetAnswer answer = answer(prefix, suffix);
                    answers.put(pair, answer);
                }
            }
        }
    }

    private boolean isPrepared() {
        return isClosed() && isConsistent() && isEvidenceClosed();
    }

    private boolean isClosed() {
        Set<Row> sRowSet = getRows(s);
        Set<Row> rRowSet = getRows(r);
        return sRowSet.containsAll(rRowSet);
    }

    private void makeClosed() {
        Set<Row> sRowSet = getRows(s);
        for (ResetLogicTimeWord word : r) {
            Row row = row(word);
            if (!sRowSet.contains(row)) {
                s.add(word);
                r.remove(word);
                for (String action : sigma) {
                    LogicTimedAction logicAction = new LogicTimedAction(action, 0d);
                    LogicTimeWord logicTimeWord = word.logicTimeWord().concat(logicAction);
                    ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
                    if (!s.contains(resetWord) && !r.contains(resetWord)) {
                        r.add(resetWord);
                    }
                }
                break;
            }
        }
        fillTable(r);
    }

    private Set<Row> getRows(Set<ResetLogicTimeWord> set) {
        Set<Row> rows = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            for (ResetLogicTimeWord word : set) {
                rows.add(row(word));
            }
        }
        return rows;
    }

    private Row row(ResetLogicTimeWord resetLogicTimeWord) {
        if (!s.contains(resetLogicTimeWord) && !r.contains(resetLogicTimeWord)) {
            return null;
        }
        Row row = new Row();
        for (LogicTimeWord suffixWord : suffixSet) {
            Pair pair = new Pair(resetLogicTimeWord, suffixWord);
            ResetAnswer answer = answers.get(pair);
            row.add(answer);
        }
        return row;
    }

    public boolean isEvidenceClosed() {
        for (ResetLogicTimeWord sPrefix : s) {
            for (LogicTimeWord suffix : suffixSet) {
                LogicTimeWord logicTimeWord = sPrefix.logicTimeWord().concat(suffix);
                ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
                if (!s.contains(resetWord) && !r.contains(resetWord)) {
                    keyWord = resetWord;
                    return false;
                }
            }
        }
        return true;
    }

    public void makeEvidClosed() {
        r.add(keyWord);
        fillTable(r);
    }

    public boolean isConsistent() {
        unConsistentCouple = new ArrayList<>();
        Set<LogicTimedAction> logicActionSet = getLastActionSet();
        List<ResetLogicTimeWord> list = getPrefixList();
        for (int i = 0; i < list.size(); i++) {
            Row row1 = row(list.get(i));
            for (int j = i + 1; j < list.size(); j++) {
                Row row2 = row(list.get(j));
                if (row1.equals(row2)) {
                    for (LogicTimedAction action : logicActionSet) {
                        LogicTimeWord word1 = list.get(i).logicTimeWord().concat(action);
                        LogicTimeWord word2 = list.get(j).logicTimeWord().concat(action);
                        ResetLogicTimeWord resetWord1 = teacher.transferWord(word1);
                        ResetLogicTimeWord resetWord2 = teacher.transferWord(word2);
                        Row newRow1 = row(resetWord1);
                        Row newRow2 = row(resetWord2);

                        if (newRow1 != null && newRow2 != null && (!newRow1.equals(newRow2))) {
                            unConsistentCouple.add(resetWord1);
                            unConsistentCouple.add(resetWord2);
                            key = action;
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    private void makeConsistent() {
        ResetLogicTimeWord word1 = unConsistentCouple.get(0);
        ResetLogicTimeWord word2 = unConsistentCouple.get(1);
        for (LogicTimeWord w : suffixSet) {
            Pair pair1 = new Pair(word1, w);
            Pair pair2 = new Pair(word2, w);

            ResetAnswer answer1 = answers.get(pair1);
            ResetAnswer answer2 = answers.get(pair2);
            if (!answer1.equals(answer2)) {
                LogicTimeWord word = consistentWord(key, w);
                suffixSet.add(word);
                break;
            }
        }
        fillTable();
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
    private static class Row {
        private List<ResetAnswer> answers;

        public Row() {
            answers = new ArrayList<>();
        }

        public int size() {
            return answers.size();
        }

        public ResetAnswer get(int i) {
            return answers.get(i);
        }

        public void add(ResetAnswer answer) {
            answers.add(answer);
        }

    }


}
