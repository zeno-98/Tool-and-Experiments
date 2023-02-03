package learn.experiment;

import learn.classificationtree.ClassificationTree;
import learn.defaultteacher.DefaultTeacher;
import ta.ota.DOTA;
import ta.ota.DOTAUtil;
import ta.ota.ResetLogicTimeWord;

import java.io.IOException;

public class ClassificationTreeExperiment {

    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\dota\\";
        String path = base + "TCP.json";
//        String base = ".\\src\\main\\resources\\dota\\";
//        String path = base + "6_4_3\\6_4_3-4.json";
        DOTA ota = DOTAUtil.getDOTAFromJsonFile(path);
        DOTAUtil.completeDOTA(ota);

        System.out.println(ota);
        DefaultTeacher teacher = new DefaultTeacher(ota);
        ClassificationTree classificationTree = new ClassificationTree("h", ota.getSigma(), teacher);

        long start = System.currentTimeMillis();

        //1、决策树初始化
        classificationTree.init();

        //2、开始学习
        classificationTree.learn();

        //3、生成假设
        DOTA hypothesis = classificationTree.buildHypothesis();
        System.out.println(hypothesis);
        //4、等价判断
        ResetLogicTimeWord ce = null;
        while (null != (ce = teacher.equivalence(hypothesis))) {
            System.out.println("反例是："+ce);
            classificationTree.refine(ce);
            hypothesis = classificationTree.buildHypothesis();
            System.out.println(hypothesis);
        }

        long end = System.currentTimeMillis();

        System.out.println("学习结束");
        System.out.println("耗时：" + (end- start) +" ms");

    }
}
