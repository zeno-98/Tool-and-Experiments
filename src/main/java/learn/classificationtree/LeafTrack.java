package learn.classificationtree;

import learn.classificationtree.node.LeafNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import ta.ota.ResetLogicAction;

@Data
@AllArgsConstructor
public class LeafTrack {

    private LeafNode source;
    private LeafNode target;
    private ResetLogicAction action;

    @Override
    public int hashCode(){
        return source.hashCode()+action.hashCode();
    }

    @Override
    public boolean equals(Object o){
        LeafTrack guard = (LeafTrack)o;
        boolean var1 = source.equals(guard.source);
        boolean var3 = action.equals(guard.action);
        return var1 && var3;
    }

    @Override
    public String toString() {
        return "Track{" +
                "source=" + source +
                ", target=" + target +
                ", word=" + action +
                '}';
    }
}
