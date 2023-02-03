package verification.uppaal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 自动机
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template implements Cloneable {
    private String name;
    private String parameter;
    private Declaration localDeclaration;
    private List<UppaalLocation> uppaalLocationList;
    private List<UppaalTransition> uppaalTransitionList;
    private UppaalLocation initUppaalLocation;

    public UppaalTransition getTransition(UppaalLocation source, UppaalLocation target) {
        List<UppaalTransition> uppaalTransitionList = getUppaalTransitionList();
        for (UppaalTransition t : uppaalTransitionList) {
            if (t.getSource() == source && t.getTarget() == target) {
                return t;
            }
        }
        return null;
    }

    public Template clone() {
        Template clone = null;
        try {
            clone = (Template) super.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

}
