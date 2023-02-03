package verification.uppaal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import verification.uppaal.model.label.AssignmentLabel;
import verification.uppaal.model.label.GuardLabel;
import verification.uppaal.model.label.SelectLabel;
import verification.uppaal.model.label.SynchronizedLabel;


/**
 * 自动机的一次状态转换
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UppaalTransition {
    private UppaalLocation source;
    private UppaalLocation target;
    private SelectLabel selectLabel;
    private GuardLabel guardLabel;
    private SynchronizedLabel synchronizedLabel;
    private AssignmentLabel assignmentLabel;

    public UppaalTransition(UppaalLocation from, UppaalLocation to) {
        this.source = from;
        this.target = to;
    }

     public UppaalTransition appendGuard(String expression) {
        String originExpression = (getGuardLabel() != null
                && getGuardLabel().getText() != null)
                ? getGuardLabel().getText() + "&&" : "";
        GuardLabel guard = new GuardLabel();
        guard.setText(originExpression + expression);
        setGuardLabel(guard);
        return this;
    }

    public UppaalTransition appendAssignment(String expression) {
        String originExpression = (getAssignmentLabel() != null
                && getAssignmentLabel().getText() != null)
                ? getAssignmentLabel().getText() + "," : "";
        AssignmentLabel assignmentLabel = new AssignmentLabel();
        assignmentLabel.setText(originExpression + expression);
        setAssignmentLabel(assignmentLabel);
        return this;
    }

}
