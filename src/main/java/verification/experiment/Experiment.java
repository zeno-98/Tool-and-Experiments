package verification.experiment;

import learn.classificationtree.ClassificationTree;
import learn.frame.Learner;
import learn.observationTable.NewObserbationTable;
import ta.ota.ResetLogicTimeWord;
import verification.frame.CheckFrame;
import verification.plugins.SequenceChecker;
import verification.teacher.UppaalTeacher;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.NTA;
import verification.uppaal.model.Template;

import java.io.IOException;
import java.util.*;

public abstract class Experiment {
    public abstract String getStatement();

    public abstract Map<String, Boolean> getSyncSendMap();

    public abstract Set<String> getResetSigma();

    public abstract List<Template> getM1();

    public abstract List<Template> getM2() throws IOException;

    public abstract String getNtaPath();

    public abstract Declaration getGlobalDeclaration();

    public abstract List<SequenceChecker> getSequenceChecker();

    public void execute(boolean tableLearner, boolean guessSigma, boolean sequenceCheck, int repeatCount) throws IOException {
        Map<String, Boolean> syncSendMap = getSyncSendMap();
        Set<String> targetSigma = new HashSet<>(syncSendMap.keySet());
        Set<String> resetSigma = getResetSigma();

        Declaration globalDeclaration = getGlobalDeclaration();
        List<Template> M1 = getM1();
        List<Template> M2 = getM2();
        List<Template> system = new ArrayList<>(M1);
        system.addAll(M2);

        NTA nta = new NTA(globalDeclaration, system);
        nta.writeToUppaalXml(getNtaPath());

        UppaalTeacher teacher = new UppaalTeacher(M1, M2, getStatement(), globalDeclaration, syncSendMap, resetSigma, targetSigma);
        if (sequenceCheck) {
            teacher.setSequencePlugin(getSequenceChecker());
        }
        Learner<ResetLogicTimeWord> learner;
        if (tableLearner) {
            NewObserbationTable table = new NewObserbationTable("assume", targetSigma, teacher);
//            MinimalObservationTable table = new MinimalObservationTable("assume", targetSigma, teacher);
//            table.setBiSimulationCheck(true);
//            table.setIncludedCheck(true);
            learner = table;
        } else {
            learner = new ClassificationTree("assume", targetSigma, teacher);
        }
        CheckFrame check = new CheckFrame(teacher, learner);
        if (guessSigma) {
            check.guessSigmas(targetSigma);
        }
        check.start(repeatCount);
    }
}
