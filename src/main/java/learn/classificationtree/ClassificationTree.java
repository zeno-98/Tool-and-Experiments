package learn.classificationtree;

import learn.classificationtree.node.InnerNode;
import learn.classificationtree.node.LeafNode;
import learn.classificationtree.node.Node;
import learn.classificationtree.node.SiftResult;
import learn.defaultteacher.BooleanAnswer;
import learn.frame.Learner;
import learn.frame.Teacher;
import lombok.Data;
import ta.Clock;
import ta.TaLocation;
import ta.TaTransition;
import ta.TimeGuard;
import ta.ota.*;

import java.util.*;

@Data
public class ClassificationTree implements Learner<ResetLogicTimeWord> {

    private String name;
    private Set<String> sigma;
    private Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher;

    private InnerNode root;
    private boolean isComplete;
    private Set<LeafTrack> leafTrackSet;
    private DOTA hypothesis;

    private Map<TaLocation, LeafNode> locationNodeMap;
    private Map<LeafNode, TaLocation> nodeLocationMap;


    public ClassificationTree(String name, Set<String> sigma, Teacher<ResetLogicTimeWord, ResetLogicTimeWord, BooleanAnswer, DOTA, LogicTimeWord> teacher) {
        this.name = name;
        this.sigma = sigma;
        this.teacher = teacher;
    }

    @Override
    public void init(Set<String> sigma) {
        this.sigma = sigma;
        init();
    }

    @Override
    public void init() {
        root = new InnerNode(LogicTimeWord.emptyWord());
        isComplete = false;
        leafTrackSet = new HashSet<>();
        LeafNode emptyLeaf = new LeafNode(ResetLogicTimeWord.emptyWord());
        ResetAnswer key = answer(emptyLeaf.getWord(), root.getWord());
        emptyLeaf.setAccpted(key.isAccept());
        emptyLeaf.setInit(true);
        emptyLeaf.setPreNode(root);
        root.add(key, emptyLeaf);
        refineSymbolTrack(emptyLeaf);
    }

    private ResetAnswer answer(ResetLogicTimeWord prefix, LogicTimeWord suffix) {
        LogicTimeWord word = prefix.logicTimeWord().concat(suffix);
        ResetLogicTimeWord resetWord = teacher.transferWord(word);
        BooleanAnswer answer = teacher.membership(resetWord);
        List<Boolean> resetList = new ArrayList<>();
        int len1 = prefix.size();
        int len2 = word.size();
        for (int i = len1; i < len2; i++) {
            ResetLogicAction resetLogicAction = resetWord.get(i);
            resetList.add(resetLogicAction.isReset());
        }
        return new ResetAnswer(resetList, answer.isAccept());
    }

    public void refineSymbolTrack(LeafNode leafNode) {
        ResetLogicTimeWord prefix = leafNode.getWord();
        for (String symbol : sigma) {
            LogicTimedAction logicAction = new LogicTimedAction(symbol, 0d);
            LogicTimeWord logicTimeWord = prefix.logicTimeWord().concat(logicAction);
            ResetLogicTimeWord resetWord = teacher.transferWord(logicTimeWord);
            LeafNode target = sift(resetWord).getLeafNode();
            ResetLogicAction resetAction = resetWord.getLastResetAction();
            LeafTrack leafTrack = new LeafTrack(leafNode, target, resetAction);
            leafTrackSet.add(leafTrack);
        }
    }

    private SiftResult sift(ResetLogicTimeWord word) {
        Node currentNode = root;
        while (currentNode.isInnerNode()) {
            InnerNode node = (InnerNode) currentNode;
            LogicTimeWord suffix = node.getWord();
            ResetAnswer key = answer(word, suffix);
            Node next = node.getChild(key);
            if (next == null) {
                boolean accept = teacher.membership(word).isAccept();
                LeafNode leafNode = new LeafNode(word, word.isEmpty(), accept);
                node.add(key, leafNode);
                leafNode.setPreNode(node);
                refineSymbolTrack(leafNode);
                return new SiftResult(leafNode, true);
            }
            currentNode = next;
        }
        return new SiftResult((LeafNode) currentNode, false);
    }

    @Override
    public void learn() {

    }

    @Override
    public void refine(ResetLogicTimeWord ce) {
        ErrorIndexResult result = errorIndexAnalyse(ce);
        int errorIndex = result.getIndex();
        TaLocation uLocation = hypothesis.reach(ce.subWord(0, errorIndex));
        LogicTimedAction action = ce.get(errorIndex).logicTimedAction();
        LeafNode sourceNode = locationNodeMap.get(uLocation);
        ResetLogicTimeWord uWord = sourceNode.getWord();
        LogicTimeWord nextWord = uWord.logicTimeWord().concat(action);
        ResetLogicTimeWord resetNextWord = teacher.transferWord(nextWord);
        SiftResult siftResult = sift(resetNextWord);
        LeafNode targetNode = siftResult.getLeafNode();
        ResetLogicAction lastResetAction = resetNextWord.getLastResetAction();
        if (result.getErrorEnum() == ErrorEnum.ResetError) {
            LeafTrack leafTrack = new LeafTrack(sourceNode, targetNode, lastResetAction);
            leafTrackSet.add(leafTrack);
            return;
        }
        if (result.getErrorEnum() == ErrorEnum.ConsistentError) {
            if (siftResult.isCompleteOperation()) {
                LeafTrack leafTrack = new LeafTrack(sourceNode, targetNode, lastResetAction);
                leafTrackSet.add(leafTrack);
                return;
            } else {
                TaLocation vLocation = nodeLocationMap.get(targetNode);
                boolean isPass = checkIsPass(uLocation, vLocation, lastResetAction);
                if (!isPass) {
                    LeafTrack leafTrack = new LeafTrack(sourceNode, targetNode, lastResetAction);
                    leafTrackSet.add(leafTrack);
                    return;
                } else {
                    LogicTimeWord suffix = ce.subWord(errorIndex + 1, ce.size()).logicTimeWord();
                    InnerNode innerNode = new InnerNode(suffix);

                    InnerNode father = targetNode.getPreNode();
                    ResetAnswer resetAnswer = null;
                    try {
                        for (ResetAnswer r : father.getKeyChildMap().keySet()) {
                            if (father.getChild(r) == targetNode) {
                                resetAnswer = r;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    father.getKeyChildMap().put(resetAnswer, innerNode);
                    innerNode.setPreNode(father);
                    targetNode.setPreNode(innerNode);
                    boolean init = resetNextWord.isEmpty();
                    boolean accept = teacher.membership(resetNextWord).isAccept();
                    LeafNode newLeafNode = new LeafNode(resetNextWord, init, accept);
                    newLeafNode.setPreNode(innerNode);
                    innerNode.add(answer(targetNode.getWord(), suffix), targetNode);
                    innerNode.add(answer(newLeafNode.getWord(), suffix), newLeafNode);
                    //refine transition
                    refineNode(targetNode);
                    //add transition
                    refineSymbolTrack(newLeafNode);
                }
            }
        }
    }

    @Override
    public boolean check(ResetLogicTimeWord counterExample) {
        try {
            errorIndexAnalyse(counterExample);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private ErrorIndexResult errorIndexAnalyse(ResetLogicTimeWord ce) {
        for (int i = 0; i <= ce.size(); i++) {
            ResetLogicTimeWord prefix = ce.subWord(0, i);
            ResetLogicTimeWord hWord = hypothesis.transferReset(prefix.logicTimeWord());
            if (!prefix.equals(hWord)) {
                return new ErrorIndexResult(i - 1, ErrorEnum.ResetError);
            }

            ResetLogicTimeWord suffix = ce.subWord(i, ce.size());
            TaLocation location = hypothesis.reach(prefix);
            ResetLogicTimeWord uWord = locationNodeMap.get(location).getWord();
            ResetAnswer key1 = answer(prefix, suffix.logicTimeWord());
            ResetAnswer key2 = answer(uWord, suffix.logicTimeWord());
            if (!key1.equals(key2)) {
                return new ErrorIndexResult(i - 1, ErrorEnum.ConsistentError);
            }
        }
        throw new RuntimeException("找不到错误位置，请检查代码");
    }

    private boolean checkIsPass(TaLocation qu, TaLocation qv, ResetLogicAction action) {
        List<TaTransition> transitionList = getHypothesis().getTransitions(qu, null, qv);
        Clock clock = hypothesis.getClock();
        boolean isPass = false;
        for (TaTransition t : transitionList) {
            Map<Clock, Double> clockDoubleMap = new HashMap<>();
            clockDoubleMap.put(clock, action.getValue());
            if (t.isPass(action.getSymbol(), clockDoubleMap)) {
                isPass = t.isReset(clock) == action.isReset();
                break;
            }
        }
        return isPass;
    }

    //把指向TargetNode的迁移重新分配
    private void refineNode(LeafNode targetNode) {
        LogicTimeWord suffix = targetNode.getPreNode().getWord();
        Iterator<LeafTrack> iterator = leafTrackSet.iterator();
        List<LeafNode> newNodeWordList = new ArrayList<>();
        while (iterator.hasNext()) {
            LeafTrack leafTrack = iterator.next();
            if (leafTrack.getTarget().equals(targetNode)) {
                ResetLogicAction resetLogicAction = leafTrack.getAction();
                LogicTimeWord logicTimeWord = leafTrack.getSource()
                        .getWord()
                        .logicTimeWord()
                        .concat(resetLogicAction.logicTimedAction());
                ResetLogicTimeWord resetLogicTimeWord = teacher.transferWord(logicTimeWord);
                ResetAnswer key = answer(resetLogicTimeWord, suffix);
                InnerNode innerNode = targetNode.getPreNode();
                LeafNode node = (LeafNode) innerNode.getChild(key);
                if (node == null) {
                    boolean accept = teacher.membership(resetLogicTimeWord).isAccept();
                    node = new LeafNode(resetLogicTimeWord,
                            resetLogicTimeWord.isEmpty(), accept);
                    node.setPreNode(innerNode);
                    innerNode.add(key, node);
                    newNodeWordList.add(node);
                }
                leafTrack.setTarget(node);
            }
        }
        for (LeafNode newLeafNode : newNodeWordList) {
            refineSymbolTrack(newLeafNode);
        }
    }

    @Override
    public DOTA buildHypothesis() {
        Clock clock = new Clock("z");

        locationNodeMap = new HashMap<>();
        nodeLocationMap = new HashMap<>();

        List<TaLocation> locationList = buildLocationList();
        List<TaTransition> transitionList = buildTransitionList(clock);

        DOTA evidenceDOTA = new DOTA(name, sigma, locationList, transitionList, clock);
        evidenceToDOTA(evidenceDOTA);
        setHypothesis(evidenceDOTA);
        return evidenceDOTA;
    }

    private List<TaTransition> buildTransitionList(Clock clock) {
        List<TaTransition> transitionList = new ArrayList<>();
        for (LeafTrack leafTrack : leafTrackSet) {
            LeafNode sourceNode = leafTrack.getSource();
            LeafNode targetNode = leafTrack.getTarget();
            TaLocation sourceLocation = nodeLocationMap.get(sourceNode);
            TaLocation targetLocation = nodeLocationMap.get(targetNode);
            ResetLogicAction action = leafTrack.getAction();
            String symbol = action.getSymbol();
            TimeGuard timeGuard = TimeGuard.bottomGuard(action.logicTimedAction());
            Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
            clockTimeGuardMap.put(clock, timeGuard);
            Set<Clock> resetClockSet = new HashSet<>();
            if (action.isReset()) {
                resetClockSet.add(clock);
            }
            TaTransition transition = new TaTransition(
                    sourceLocation, targetLocation, symbol, clockTimeGuardMap, resetClockSet);
            if (!transitionList.contains(transition)) {
                transitionList.add(transition);
            }
        }
        return transitionList;
    }

    private List<TaLocation> buildLocationList() {
        List<LeafNode> nodeList = leafList();
        List<TaLocation> locationList = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            LeafNode node = nodeList.get(i);
            TaLocation location = new TaLocation(
                    String.valueOf(i + 1),
                    String.valueOf(i + 1),
                    node.isInit(),
                    node.isAccpted());
            locationList.add(location);
            nodeLocationMap.put(node, location);
            locationNodeMap.put(location, node);
        }
        return locationList;
    }

    private List<LeafNode> leafList() {
        Map<ResetLogicTimeWord, LeafNode> leafMap = getLeafMap();
        return new ArrayList<>(leafMap.values());
    }

    private Map<ResetLogicTimeWord, LeafNode> getLeafMap() {
        Map<ResetLogicTimeWord, LeafNode> leafMap = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            if (node.isLeaf()) {
                LeafNode leaf = (LeafNode) node;
                ResetLogicTimeWord suffix = leaf.getWord();
                leafMap.put(suffix, leaf);
            } else {
                InnerNode innerNode = (InnerNode) node;
                queue.addAll(innerNode.getChildList());
            }
        }
        return leafMap;
    }

    @Override
    public DOTA getFinalHypothesis() {
        return hypothesis;
    }

    @Override
    public void show() {

    }
}
