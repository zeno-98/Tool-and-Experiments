package verification.experiment.autosar2;

public class Experiment2_4 extends Experiment2 {

    @Override
    public String getStatement() {
        return "A[] buffer2.count <= buffer2.len";
    }
}
