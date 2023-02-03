package learn.classificationtree.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ta.timedword.TimedWord;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class Node<T extends TimedWord> {
    private T word;

    public boolean isLeaf(){
        return this instanceof LeafNode;
    }

    public boolean isInnerNode(){
        return this instanceof InnerNode;
    }
}
