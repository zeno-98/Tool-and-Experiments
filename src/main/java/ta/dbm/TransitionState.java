package ta.dbm;

import lombok.Data;

@Data
public class TransitionState extends BaseState {
    private String symbol;
    private LocationState preState;

    public TransitionState(DBM dbm, String symbol) {
        super(dbm);
        this.symbol = symbol;
    }

    public TransitionState(DBM dbm, String symbol, LocationState preState) {
        super(dbm);
        this.symbol = symbol;
        this.preState = preState;
    }
}
