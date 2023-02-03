package verification.experiment.autosar2;

import verification.experiment.Experiment;
import verification.plugins.SequenceChecker;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;

import java.util.*;

public abstract class Experiment2 extends Experiment {

    @Override
    public Map<String, Boolean> getSyncSendMap() {
        Map<String, Boolean> syncSendMap = new HashMap<>();
        syncSendMap.put("runnable1_start", true);
        syncSendMap.put("task_start", true);
        return syncSendMap;
    }

    @Override
    public Set<String> getResetSigma() {
        Set<String> set = new HashSet<>();
        set.add("runnable1_start");
        set.add("task_start");
        return set;
    }

    @Override
    public List<Template> getM1() {
        List<Template> list = new ArrayList<>();
        list.add(AutosarEx2Util.buildRunnable1());
        list.add(AutosarEx2Util.buildRunnable2());
        list.add(AutosarEx2Util.buildRunnable3());
        list.add(AutosarEx2Util.buildRunnable4());
        list.add(AutosarEx2Util.buildRunnable5());
        list.add(AutosarEx2Util.buildRunnable6());
        list.add(AutosarEx2Util.buildBuffer1());
        list.add(AutosarEx2Util.buildBuffer2());
        list.add(AutosarEx2Util.buildBuffer3());
        list.add(AutosarEx2Util.buildBuffer4());
        list.add(AutosarEx2Util.buildBuffer5());
        list.add(AutosarEx2Util.buildRTE());
        list.add(AutosarEx2Util.buildTask());
        return list;
    }

    @Override
    public List<Template> getM2() {
        return Collections.singletonList(AutosarEx2Util.buildSchedule());
    }

    @Override
    public String getNtaPath() {
        return ".\\src\\main\\resources\\verification\\autosar_ex3-source.xml";
    }

    @Override
    public Declaration getGlobalDeclaration() {
        return AutosarEx2Util.getGlobalDeclaration();
    }

    @Override
    public List<SequenceChecker> getSequenceChecker() {
        SequenceChecker plugin = new SequenceChecker(false);
        String s0 = "[startWith]:(runnable1_start,task_start)";
        String s1 = "[startWith]:(runnable1_start,runnable1_start)";
        String s2 = "[startWith]:(runnable1_start,runnable1_start,task_start)";
        String s3 = "[startWith]:(runnable1_start,runnable1_start,runnable1_start,task_start)";
        plugin.add(s0);
        plugin.add(s1);
        plugin.add(s2);
        plugin.add(s3);
        return Collections.singletonList(plugin);
    }
}
