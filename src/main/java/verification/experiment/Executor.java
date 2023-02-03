package verification.experiment;

import verification.experiment.autosar1.mutileM1.Experiment1_1;

import java.io.IOException;

public class Executor {
    public static void main(String[] args) throws IOException {
        // Verification experiments for AUTOSAR-1 where M_1 is a DOTA
        // import verification.experiment.autosar1.mutileM1.Experiment1_1;
        // ↓        A[] (buffer1.count >=0)
        // Experiment e = new Experiment1_single_1();
        // ↓        A[] (buffer1.count <= buffer1.len)
        // Experiment e = new Experiment1_single_2();
        // ↓        A[] (buffer2.count >=0)
        // Experiment e = new Experiment1_single_3();
        // ↓        A[] (buffer2.count <= buffer2.len)
        // Experiment e = new Experiment1_single_4();

        // Verification experiments for AUTOSAR-1 where M_1 is a composition of DOTAs
        // ↓        A[] (buffer1.count >=0)
        // Experiment e = new Experiment1_1();
        // ↓        A[] (buffer1.count <= buffer1.len)
        // Experiment e = new Experiment1_2();
        // ↓        A[] (buffer2.count >=0)
        // Experiment e = new Experiment1_3();
        // ↓        A[] (buffer2.count <= buffer2.len)
        // Experiment e = new Experiment1_4();
        // ↓        A[] not (runnable1.s2 and runnable2.s2)
        // Experiment e = new Experiment1_5();
        // ↓        A[] not (runnable3.s2 and runnable4.s2)
        // Experiment e = new Experiment1_6();

        // Verification experiments for AUTOSAR-2
        // ↓        A[] (buffer1.count >=0)
        // Experiment e = new Experiment2_1();
        // ↓        A[] (buffer1.count <= buffer1.len)
        // Experiment e = new Experiment2_2();
        // ↓        A[] (buffer2.count >=0)
        // Experiment e = new Experiment2_3();
        // ↓        A[] (buffer2.count <= buffer2.len)
//         Experiment e = new Experiment2_4();

        // Verification experiments for AUTOSAR-3
        // ↓        A[] (buffer1.count >=0)
//         Experiment e = new Experiment3_1();
        // ↓        A[] (buffer2.count >=0)
//         Experiment e = new Experiment3_2();
        // ↓        A[] (buffer3.count >=0)
        // Experiment e = new Experiment3_3();


        Experiment e = new Experiment1_1();
        e.execute(true, false, false, 1);
    }
}
