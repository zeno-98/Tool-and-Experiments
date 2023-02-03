package verification.uppaal.model.builder;


import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.label.InvariantLabel;

public class UppaalLocationBuilder {
    private String name;
    private String id;
    private InvariantLabel invariantLabel;
    private boolean urgent;
    private boolean committed;

    public UppaalLocationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UppaalLocationBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public UppaalLocationBuilder setInvariantLabel(String text) {
        InvariantLabel invariantLabel = new InvariantLabel();
        invariantLabel.setText(text);
        this.invariantLabel = invariantLabel;
        return this;
    }

    public UppaalLocationBuilder setUrgent(boolean urgent) {
        this.urgent = urgent;
        return this;
    }

    public UppaalLocationBuilder setCommitted(boolean committed) {
        this.committed = committed;
        return this;
    }

    public UppaalLocation createLocation() {
        return new UppaalLocation(id,name,invariantLabel,urgent,committed);
    }
}