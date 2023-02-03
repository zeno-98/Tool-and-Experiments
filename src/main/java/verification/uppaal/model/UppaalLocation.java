package verification.uppaal.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import verification.uppaal.model.label.InvariantLabel;


/**
 * 自动机的一个状态点
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UppaalLocation {
	private String id;
    private String name;
    private InvariantLabel invariantLabel;
    private boolean urgent;
    private boolean committed;

    public UppaalLocation(String name) {
        this.name = name;
        this.id = name;
    }

}
