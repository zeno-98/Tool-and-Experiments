package verification.experiment.autosar3;

import verification.plugins.SequenceChecker;
import verification.uppaal.model.Template;
import verification.util.VerificationUtil;

import java.io.IOException;
import java.util.*;

public class Experiment3_1 extends Experiment3 {
    @Override
    public String getStatement() {
        return "A[] buffer1.count >= 0";
    }

    @Override
    public Map<String, Boolean> getSyncSendMap() {
        Map<String, Boolean> sendMap = new HashMap<>();
        sendMap.put("task1_start", true);
        sendMap.put("task2_start", true);
        sendMap.put("task3_start", true);
        return sendMap;
    }

    @Override
    public Set<String> getResetSigma() {
        Set<String> resetSigma = new HashSet<>();
        resetSigma.add("task3_start");
        return resetSigma;
    }

    @Override
    public List<Template> getM1() {
        List<Template> list = new ArrayList<>();
        list.add(AutosarEx3Util.buildRunnable1());
        list.add(AutosarEx3Util.buildRunnable2());
        list.add(AutosarEx3Util.buildRunnable3());
        list.add(AutosarEx3Util.buildRunnable4());
        list.add(AutosarEx3Util.buildRunnable5());
        list.add(AutosarEx3Util.buildRunnable6());
        list.add(AutosarEx3Util.buildRunnable7());
        list.add(AutosarEx3Util.buildBuffer1());
        list.add(AutosarEx3Util.buildBuffer2());
        list.add(AutosarEx3Util.buildBuffer3());
        list.add(AutosarEx3Util.buildTask1());
        list.add(AutosarEx3Util.buildTask2());
        list.add(AutosarEx3Util.buildTask3());
        return list;
    }

    @Override
    public List<Template> getM2() throws IOException {
        List<Template> list = new ArrayList<>();
        Template template = AutosarEx3Util.buildSchedule();
        VerificationUtil.refine(template, getSyncSendMap(), true);
        list.add(template);
        return list;
    }

    @Override
    public List<SequenceChecker> getSequenceChecker() {
//        String s0 = "[startWith]:(task1_start)";
//        String s0 = "[beforeAfter]:(runnable4_start, runnable4_finish)";
        String s1 = "[beforeAfter]:(task1_start, task3_start)";
//        String s1 = "[startWith]:(runnable1_start,runnable1_finish)";
//        String s3 = "[startWith]:(runnable1_start)";
//        String s4 = "[startWith]:(runnable1_finish,runnable2_start)";

        SequenceChecker plugin = new SequenceChecker(true);
//        plugin.add(s0);
        plugin.add(s1);
//        plugin.add(s1);
//        plugin.add(s3);
//        plugin.add(s4);
        return Collections.singletonList(plugin);
    }
}
