package verification.uppaal.model.builder;

import verification.uppaal.constants.UppaalConstants;
import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.UppaalTransition;
import verification.uppaal.model.label.AssignmentLabel;
import verification.uppaal.model.label.GuardLabel;
import verification.uppaal.model.label.SelectLabel;
import verification.uppaal.model.label.SynchronizedLabel;

import java.util.Map;

/**
 * 快速构建Transition的Builder
 */
public class UppaalTransitionBuilder {

    private final UppaalTransition uppaalTransition;

    public UppaalTransitionBuilder(UppaalLocation from, UppaalLocation to) {
        this.uppaalTransition = new UppaalTransition(from, to);
    }

    public UppaalTransitionBuilder(UppaalTransition uppaalTransition) {
        this.uppaalTransition = uppaalTransition;
    }

    /**
     * 快速构建sync类型的Transition实体
     * @param syncName 同步的名字
     * @param suffix 表示接受消息还是发送event ? 表示发送，!表示接收
     * @param channelMap 同步信号的发送/接收map
     * @return
     */
    public UppaalTransitionBuilder addSync(String syncName, String suffix, Map<String, String> channelMap) {
        channelMap.put(syncName, UppaalConstants.CHAN);
        SynchronizedLabel synchronizedLabel = new SynchronizedLabel();
        synchronizedLabel.setText(syncName + suffix);
        uppaalTransition.setSynchronizedLabel(synchronizedLabel);
        return this;
    }

    public UppaalTransitionBuilder addSync(String syncName, String suffix) {
        addSync(syncName + suffix);
        return this;
    }

    public UppaalTransitionBuilder addSync(String syncName) {
        SynchronizedLabel synchronizedLabel = new SynchronizedLabel();
        synchronizedLabel.setText(syncName);
        uppaalTransition.setSynchronizedLabel(synchronizedLabel);
        return this;
    }

    /**
     * 向transition中添加guard
     */
    public UppaalTransitionBuilder addGuard(String label) {
        GuardLabel guard = new GuardLabel();
        guard.setText(label);
        uppaalTransition.setGuardLabel(guard);
        return this;
    }

    public UppaalTransitionBuilder addSelect(String var, String arrayText){
        String text = var+":"+arrayText;
        SelectLabel selectLabel = new SelectLabel(text);
        uppaalTransition.setSelectLabel(selectLabel);
        return this;
    }

    /**
     *向transition 中添加assignment
     */
    public UppaalTransitionBuilder addAssignment(String label) {
        AssignmentLabel assignmentLabel4 = new AssignmentLabel();
        assignmentLabel4.setText(label);
        uppaalTransition.setAssignmentLabel(assignmentLabel4);
        return this;
    }

    /**
     * 向 uppaalTransition 中增加guard表达式
     */
    public UppaalTransitionBuilder appendGuard(String expression) {
        String originExpression = (uppaalTransition.getGuardLabel() != null
        		&& uppaalTransition.getGuardLabel().getText() != null)
        		? uppaalTransition.getGuardLabel().getText() + "&&" : "";
        return addGuard(originExpression + expression);
    }

    public UppaalTransition getUppaalTransition() {
        return uppaalTransition;
    }

    public UppaalTransitionBuilder appendAssignment(String expression) {
        String originExpression = (uppaalTransition.getAssignmentLabel() != null
                && uppaalTransition.getAssignmentLabel().getText() != null)
                ? uppaalTransition.getAssignmentLabel().getText() + "," : "";
        AssignmentLabel assignmentLabel = new AssignmentLabel();
        assignmentLabel.setText(originExpression + expression);
        uppaalTransition.setAssignmentLabel(assignmentLabel);
        return this;
    }
}
