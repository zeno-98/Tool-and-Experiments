package verification.experiment.autosar1.mutileM1;

import verification.experiment.autosar1.Experiment1;

public class Experiment1_5 extends Experiment1 {

    @Override
    public String getStatement() {
        return "A[] not (runnable1.s2 and runnable2.s2)";
    }
}
