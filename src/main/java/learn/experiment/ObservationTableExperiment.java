package learn.experiment;

import learn.defaultteacher.DefaultTeacher;
import learn.observationTable.ObservationTable;
import ta.ota.DOTA;
import ta.ota.DOTAUtil;
import ta.ota.ResetLogicTimeWord;

import java.io.IOException;

public class ObservationTableExperiment {
    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\dota\\";
        String path = base + "TCP.json";
//        String base = ".\\src\\main\\resources\\dota\\";
//        String path = base + "6_4_3\\6_4_3-4.json";
        DOTA ota = DOTAUtil.getDOTAFromJsonFile(path);
        DOTAUtil.completeDOTA(ota);

        System.out.println(ota);
        DefaultTeacher teacher = new DefaultTeacher(ota);
        ObservationTable observationTable = new ObservationTable("h", ota.getSigma(), teacher);

//        ResetLogicTimeWord word = ResetLogicTimeWord.emptyWord();
//        word = word.concat(new ResetLogicAction("a", 8.5, false));
////        word = word.concat(new ResetLogicAction("c", 8.0, true));
//        word = word.concat(new ResetLogicAction("a", 5.5, true));
//        BooleanAnswer result = teacher.membership(word);

        long start = System.currentTimeMillis();
        //自定义学习流程
        //1、观察表初始化
        observationTable.init();

        //2、开始学习
        observationTable.learn();
        observationTable.show();

        //3、生成假设
        DOTA hypothesis = observationTable.buildHypothesis();
        System.out.println(hypothesis);
        //4、等价判断
        ResetLogicTimeWord ce = null;
        while (null != (ce = teacher.equivalence(hypothesis))) {
            System.out.println("ctx:"+ce);
            observationTable.refine(ce);
            observationTable.show();
            hypothesis = observationTable.buildHypothesis();
            System.out.println(hypothesis);
        }

        long end = System.currentTimeMillis();
        int eq = teacher.getEquivalenceQuery().getCount();

        System.out.println("study complete");
        System.out.println("eq = "+eq);
        System.out.println("cost time:" + (end- start) +" ms");

    }

}
