package learn.classificationtree.node;

import learn.classificationtree.ResetAnswer;
import lombok.Data;
import ta.ota.LogicTimeWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class InnerNode extends Node<LogicTimeWord> {
    private InnerNode preNode;
    private Map<ResetAnswer, Node> keyChildMap = new HashMap<>();

    public InnerNode(LogicTimeWord word) {
        super(word);
    }
    public void add(ResetAnswer key, Node node) {
        keyChildMap.put(key, node);
    }
    public Node getChild(ResetAnswer key) {
        Node node = keyChildMap.get(key);
        return node;
    }
    public List<Node> getChildList() {
        return new ArrayList<>(keyChildMap.values());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
