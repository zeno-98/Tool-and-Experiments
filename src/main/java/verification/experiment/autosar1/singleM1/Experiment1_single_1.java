package verification.experiment.autosar1.singleM1;

import verification.experiment.Experiment;
import verification.experiment.autosar1.AutosarEx1Util;
import verification.plugins.SequenceChecker;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;

import java.util.*;

public class Experiment1_single_1 extends Experiment {
    @Override
    public String getStatement() {
        return "A[] (buffer1.count >=0)";
    }

    @Override
    public Map<String, Boolean> getSyncSendMap() {
        Map<String, Boolean> syncSendMap = new HashMap<>();
        syncSendMap.put("read1", true);
        syncSendMap.put("write1", true);
        return syncSendMap;
    }

    @Override
    public Set<String> getResetSigma() {
        Set<String> set = new HashSet<>();
        set.add("read1");
        set.add("write1");
        return set;
    }

    @Override
    public List<Template> getM1() {
        return Collections.singletonList(AutosarEx1Util.buildBuffer1());
    }

    @Override
    public List<Template> getM2() {
        List<Template> list = new ArrayList<>();
        list.add(AutosarEx1Util.buildRunnable1());
        list.add(AutosarEx1Util.buildRunnable2());
        list.add(AutosarEx1Util.buildRunnable3());
        list.add(AutosarEx1Util.buildRunnable4());
//        list.add(AutosarEx2Util.buildBuffer1());
        list.add(AutosarEx1Util.buildBuffer2());
        list.add(AutosarEx1Util.buildSchedule2());
        list.add(AutosarEx1Util.buildSchedule1());
        return list;
    }

    @Override
    public String getNtaPath() {
        return ".\\src\\main\\resources\\verification\\autosar_ex2-source.xml";
    }

    @Override
    public Declaration getGlobalDeclaration() {
        Declaration globalDeclaration = new Declaration();
        globalDeclaration.put("read1,write1", "chan");
        globalDeclaration.put("read2,write2", "chan");
        globalDeclaration.put("runnable1_start", "chan");
        globalDeclaration.put("runnable2_start", "chan");
        globalDeclaration.put("runnable3_start", "chan");
        globalDeclaration.put("runnable4_start", "chan");
        return globalDeclaration;
    }

    @Override
    public List<SequenceChecker> getSequenceChecker() {
        SequenceChecker plugin = new SequenceChecker(false);
        String s1 = "[startWith]:(runnable1_start,runnable1_start)";
        String s2 = "[startWith]:(runnable1_start,runnable1_start,runnable2_start)";
        String s3 = "[startWith]:(runnable1_start,runnable1_start,runnable1_start,runnable2_start)";
        String s4 = "[startWith]:(runnable1_start,runnable2_start)";
        plugin.add(s1);
        plugin.add(s2);
        plugin.add(s3);
        plugin.add(s4);
        return Collections.singletonList(plugin);
    }
}
