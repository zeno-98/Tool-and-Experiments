package verification.experiment.autosar1.mutileM1;

import verification.experiment.autosar1.AutosarEx1Util;
import verification.experiment.autosar1.Experiment1;
import verification.plugins.SequenceChecker;
import verification.uppaal.model.Template;

import java.util.*;

public class Experiment1_6 extends Experiment1 {

    @Override
    public String getStatement() {
        return "A[] not (runnable3.s2 and runnable4.s2)";
    }

    @Override
    public Map<String, Boolean> getSyncSendMap() {
        Map<String, Boolean> syncSendMap = new HashMap<>();
        syncSendMap.put("runnable3_start", true);
        syncSendMap.put("runnable4_start", true);
        return syncSendMap;
    }

    @Override
    public Set<String> getResetSigma() {
        Set<String> set = new HashSet<>();
        set.add("runnable3_start");
        set.add("runnable4_start");
        return set;
    }

    @Override
    public List<SequenceChecker> getSequenceChecker() {
        SequenceChecker plugin = new SequenceChecker(false);
        String s0 = "[startWith]:(runnable3_start,runnable4_start)";
        String s1 = "[startWith]:(runnable3_start,runnable3_start)";
        String s2 = "[startWith]:(runnable3_start,runnable3_start,runnable4_start)";
        String s3 = "[startWith]:(runnable3_start,runnable3_start,runnable3_start,runnable4_start)";
        plugin.add(s0);
        plugin.add(s1);
        plugin.add(s2);
        plugin.add(s3);
        return Collections.singletonList(plugin);
    }

    @Override
    public List<Template> getM1() {
        List<Template> list = new ArrayList<>();
        list.add(AutosarEx1Util.buildRunnable1());
        list.add(AutosarEx1Util.buildRunnable2());
        list.add(AutosarEx1Util.buildRunnable3());
        list.add(AutosarEx1Util.buildRunnable4());
        list.add(AutosarEx1Util.buildBuffer1());
        list.add(AutosarEx1Util.buildBuffer2());
        list.add(AutosarEx1Util.buildSchedule1());
        return list;
    }

    @Override
    public List<Template> getM2() {
        return Collections.singletonList(AutosarEx1Util.buildSchedule2());
    }
}
