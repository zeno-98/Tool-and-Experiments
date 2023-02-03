package learn.observationTable.minimal;

import learn.classificationtree.ResetAnswer;
import learn.defaultteacher.BooleanAnswer;
import learn.frame.Learner;
import learn.frame.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ta.ota.DOTA;
import ta.ota.LogicTimeWord;
import ta.ota.LogicTimedAction;
import ta.ota.ResetLogicTimeWord;

import java.util.*;

public abstract class AbstractObservationTable implements Learner<ResetLogicTimeWord> {
    public long consistentTime = 0;
    protected String name;
    protected Set<String> sigma;
    protected Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher;
    protected Set<ResetLogicTimeWord> s;
    protected Set<ResetLogicTimeWord> r;
    protected List<LogicTimeWord> suffixSet;
    protected Map<Pair, ResetAnswer> answers;
    protected DOTA hypothesis;

    public AbstractObservationTable(String name, Set<String> sigma, Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher) {
        this.name = name;
        this.sigma = sigma;
        this.teacher = teacher;
    }

    public Set<ResetLogicTimeWord> getPrefixSet() {
        Set<ResetLogicTimeWord> sr = new HashSet<>();
        sr.addAll(s);
        sr.addAll(r);
        return sr;
    }

    public List<ResetLogicTimeWord> getPrefixList() {
        List<ResetLogicTimeWord> logicTimeWordList = new ArrayList<>();
        logicTimeWordList.addAll(s);
        logicTimeWordList.addAll(r);
        return logicTimeWordList;
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
                makeEvidenceClosed(keyWord);
                continue;
            }
            ConsistentResult consistentResult;
            consistentResult = isConsistent();
            if (consistentResult != null) {
                makeConsistent(consistentResult);
                continue;
            }
            break;
        }
    }

    protected abstract Row isClosed();

    protected abstract void makeClosed(Row row);

    protected abstract ResetLogicTimeWord isEvidenceClosed();

    protected abstract void makeEvidenceClosed(ResetLogicTimeWord keyWord);

    protected abstract ConsistentResult isConsistent();

    protected abstract void makeConsistent(ConsistentResult consistentResult);

    protected abstract ResetAnswer answer(ResetLogicTimeWord prefix, LogicTimeWord suffix);

    @Override
    public void refine(ResetLogicTimeWord counterExample) {
        Set<ResetLogicTimeWord> prefixesSet = counterExample.getAllPrefixes();
        prefixesSet.removeAll(s);
        r.addAll(prefixesSet);
        learn();
    }

    @Override
    public DOTA getFinalHypothesis() {
        return hypothesis;
    }

    @Override
    public boolean check(ResetLogicTimeWord counterExample) {
        return false;
    }

    protected void fillTable() {
        fillTable(s);
        fillTable(r);
    }

    protected void fillTable(Set<ResetLogicTimeWord> set) {
        for (ResetLogicTimeWord prefix : set) {
            fillTable(prefix);
        }
    }

    protected void fillTable(ResetLogicTimeWord prefix) {
        for (LogicTimeWord suffix : suffixSet) {
            Pair pair = new Pair(prefix, suffix);
            if (!answers.containsKey(pair)) {
                ResetAnswer answer = answer(prefix, suffix);
                answers.put(pair, answer);
            }
        }
    }

    @Data
    @AllArgsConstructor
    protected static class ConsistentResult {
        private ResetLogicTimeWord fatherRowWord;
        private ResetLogicTimeWord childRowWord;
        private LogicTimedAction keyAction;
    }

    @Data
    @AllArgsConstructor
    protected static class Pair {
        private ResetLogicTimeWord prefix;
        private LogicTimeWord suffix;

        public LogicTimeWord timeWord() {
            LogicTimeWord pre = prefix.logicTimeWord();
            return pre.concat(suffix);
        }
    }

    @Data
    protected static class Row {
        private List<ResetAnswer> answers;
        private boolean invalidAnswer;
        private ResetLogicTimeWord prefix;
        private int invalidCount;
        @EqualsAndHashCode.Exclude
        private int fatherCount;

        public Row(ResetLogicTimeWord prefix) {
            this.prefix = prefix;
            answers = new ArrayList<>();
            invalidAnswer = false;
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
                invalidAnswer = true;
            }
            answers.add(answer);
        }
    }


}
