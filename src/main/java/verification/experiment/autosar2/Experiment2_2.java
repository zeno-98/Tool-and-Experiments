package verification.experiment.autosar2;

public class Experiment2_2 extends Experiment2 {

    @Override
    public String getStatement() {
        return "A[] buffer1.count <= buffer1.len";
    }
}
