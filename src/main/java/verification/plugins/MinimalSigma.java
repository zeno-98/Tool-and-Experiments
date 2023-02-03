package verification.plugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinimalSigma {
    private final List<String> targetSigmas;
    private final List<List<String>> sigmaList;
    private Set<String> curSigma;
    private int idx;

    public MinimalSigma(Set<String> sigma) {
        targetSigmas = new ArrayList<>(sigma);
        targetSigmas.sort(String::compareToIgnoreCase);
        sigmaList = new ArrayList<>(2 ^ (targetSigmas.size()) - 1);
        for (int i = 1; i <= targetSigmas.size(); i++) {
            backtrace(new ArrayList<>(), i, 0);
        }
        idx = 0;
    }

    public void init() {
        idx = 0;
    }

    public boolean hasNext() {
        return idx < sigmaList.size();
    }

    public Set<String> getNext() {
//        Set<String> specialSigma = new HashSet<>();
//        specialSigma.add("runnable1_finish");
//        specialSigma.add("runnable2_finish");
//        specialSigma.add("runnable2_start");
//        return specialSigma;
        if (idx >= sigmaList.size()) {
            return null;
        }
        List<String> result = sigmaList.get(idx);
        idx += 1;
        curSigma = new HashSet<>(result);
        return getCur();
    }

    public Set<String> getCur() {
        if (idx == 0) return getNext();
        return curSigma;
    }

    private void backtrace(List<String> tmp, int maxCount, int beginIdx) {
        if (tmp.size() == maxCount) {
            sigmaList.add(new ArrayList<>(tmp));
            return;
        }

        for (int i = beginIdx; i < targetSigmas.size(); i++) {
            tmp.add(targetSigmas.get(i));
            backtrace(tmp, maxCount, i + 1);
            tmp.remove(tmp.size() - 1);
        }
    }

}
