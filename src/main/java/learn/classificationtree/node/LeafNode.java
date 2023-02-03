package learn.classificationtree.node;

import lombok.Data;
import ta.ota.ResetLogicTimeWord;

@Data
public class LeafNode extends Node<ResetLogicTimeWord> {
    private boolean init;
    private boolean accpted;
    private InnerNode preNode;

    public LeafNode(ResetLogicTimeWord word) {
        super(word);
    }

    public LeafNode(ResetLogicTimeWord word, boolean init, boolean accpted) {
        super(word);
        this.init = init;
        this.accpted = accpted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeafNode)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        LeafNode leafNode = (LeafNode) o;

        if (init != leafNode.init) {
            return false;
        }
        return accpted == leafNode.accpted;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (init ? 1 : 0);
        result = 31 * result + (accpted ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
