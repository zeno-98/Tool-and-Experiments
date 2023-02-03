package verification.experiment.autosar1.mutileM1;

import verification.experiment.autosar1.Experiment1;

public class Experiment1_4 extends Experiment1 {

    @Override
    public String getStatement() {
        return "A[] (buffer2.count <= buffer2.len)";
    }
}
