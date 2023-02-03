package verification.experiment.autosar3;

import ta.dfa.DFA;
import ta.dfa.DFAUtil;
import ta.ota.DOTA;
import ta.ota.DOTAUtil;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;
import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.UppaalTransition;
import verification.uppaal.model.builder.TemplateBuilder;
import verification.uppaal.model.builder.UppaalTransitionBuilder;
import verification.util.UppaalModelUtil;

import java.io.IOException;

public class AutosarEx3Util {

    public static Template buildRunnable1() {
        String name = "runnable1";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "active1");
        UppaalLocation s5 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable1_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("write2", "!")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("write1", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s1)
                .addSync("runnable1_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildRunnable2() {
        String name = "runnable2";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "active1");
        UppaalLocation s5 = UppaalModelUtil.buildUppaalLocation(name, "implicW");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable2_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read1", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("write2", "!")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s1)
                .addSync("runnable2_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildRunnable3() {
        String name = "runnable3";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "active1");
        UppaalLocation s5 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable3_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("read3", "!")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("read2", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s1)
                .addSync("runnable3_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildRunnable4() {
        String name = "runnable4";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "active1");
        UppaalLocation s5 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable4_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("write3", "!")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("write2", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s1)
                .addSync("runnable4_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildRunnable5() {
        String name = "runnable5";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable5_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("write3", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s1)
                .addSync("runnable5_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4)
                .createTemplate();
    }

    public static Template buildRunnable6() {
        String name = "runnable6";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable6_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("write3", "!")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s1)
                .addSync("runnable6_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4)
                .createTemplate();
    }

    public static Template buildRunnable7() {
        String name = "runnable7";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicR");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active");
        UppaalLocation s4 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW");
        UppaalLocation s5 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implicW1");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable7_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read3", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("read3", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s1)
                .addSync("runnable7_finish", "!")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildBuffer1() {
        String name = "buffer1";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "error");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addSync("read1", "?")
                .addGuard("len >= 0")
                .addAssignment("count = count-1")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addSync("write1", "?")
                .addGuard("count <= len")
                .addAssignment("count = count+1")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("count>len")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("count<0")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s2, s2)
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=3", "int")
                .addInitLocation(s1)
                .addLocations(s2)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildBuffer2() {
        String name = "buffer2";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "error");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addSync("read2", "?")
                .addGuard("len >= 0")
                .addAssignment("count = count-1")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addSync("write2", "?")
                .addGuard("count <= len")
                .addAssignment("count = count+1")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("count>len")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("count<0")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s2, s2)
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=300", "int")
                .addInitLocation(s1)
                .addLocations(s2)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildBuffer3() {
        String name = "buffer3";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "error");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addSync("read3", "?")
                .addGuard("len >= 0")
                .addAssignment("count = count-1")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addSync("write3", "?")
                .addGuard("count <= len")
                .addAssignment("count = count+1")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("count>len")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("count<0")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s2, s2)
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=3", "int")
                .addInitLocation(s1)
                .addLocations(s2)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Template buildTask1() {
        String name = "task1";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4");
        UppaalLocation s5 = UppaalModelUtil.buildUppaalLocation(name, "s5");
        UppaalLocation s6 = UppaalModelUtil.buildUppaalLocation(name, "s6");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("task1_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("runnable1_start", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("runnable1_finish", "?")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("runnable2_start", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s6)
                .addSync("runnable2_finish", "?")
                .getUppaalTransition();
        UppaalTransition t6 = new UppaalTransitionBuilder(s6, s1)
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5, s6)
                .addTransitions(t1, t2, t3, t4, t5, t6)
                .createTemplate();
    }

    public static Template buildTask2() {
        String name = "task2";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4");
        UppaalLocation s5 = UppaalModelUtil.buildUppaalLocation(name, "s5");
        UppaalLocation s6 = UppaalModelUtil.buildUppaalLocation(name, "s6");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("task2_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("runnable4_start", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("runnable4_finish", "?")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("runnable3_start", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s6)
                .addSync("runnable3_finish", "?")
                .getUppaalTransition();
        UppaalTransition t6 = new UppaalTransitionBuilder(s6, s1)
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5, s6)
                .addTransitions(t1, t2, t3, t4, t5, t6)
                .createTemplate();
    }

    public static Template buildTask3() {
        String name = "task3";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4");
        UppaalLocation s5 = UppaalModelUtil.buildUppaalLocation(name, "s5");
        UppaalLocation s6 = UppaalModelUtil.buildUppaalLocation(name, "s6");
        UppaalLocation s7 = UppaalModelUtil.buildUppaalLocation(name, "s7");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("task3_start", "?")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("runnable6_start", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("runnable6_finish", "?")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("runnable5_start", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s6)
                .addSync("runnable5_finish", "?")
                .getUppaalTransition();
        UppaalTransition t6 = new UppaalTransitionBuilder(s6, s7)
                .addSync("runnable7_start", "!")
                .getUppaalTransition();
        UppaalTransition t7 = new UppaalTransitionBuilder(s7, s1)
                .addSync("runnable7_finish", "?")
                .getUppaalTransition();

        return new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5, s6, s7)
                .addTransitions(t1, t2, t3, t4, t5, t6, t7)
                .createTemplate();
    }

    public static Template buildSchedule() {
        String name = "schedule";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1", "x<=20");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=30");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3", "x<=40");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4", "x<=60");
        UppaalLocation s5 = UppaalModelUtil.buildUppaalLocation(name, "s5", "x<=60");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("task1_start", "!")
                .addGuard("x==20")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("task2_start", "!")
                .addGuard("x==30")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("task1_start", "!")
                .addGuard("x==40")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s5)
                .addSync("task2_start", "!")
                .addGuard("x==60")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s5, s1)
                .addSync("task3_start", "!")
                .addGuard("x==60")
                .addAssignment("x=0")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3, s4, s5)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();
    }

    public static Declaration buildGlobalDeclaration() {
        Declaration globalDeclaration = new Declaration();
        globalDeclaration.put("read1,write1", "chan");
        globalDeclaration.put("read2,write2", "chan");
        globalDeclaration.put("read3,write3", "chan");
        globalDeclaration.put("runnable1_start,runnable1_finish", "chan");
        globalDeclaration.put("runnable2_start,runnable2_finish", "chan");
        globalDeclaration.put("runnable3_start,runnable3_finish", "chan");
        globalDeclaration.put("runnable4_start,runnable4_finish", "chan");
        globalDeclaration.put("runnable5_start,runnable5_finish", "chan");
        globalDeclaration.put("runnable6_start,runnable6_finish", "chan");
        globalDeclaration.put("runnable7_start,runnable7_finish", "chan");
        globalDeclaration.put("task1_start,task1_finish", "chan");
        globalDeclaration.put("task2_start,task2_finish", "chan");
        globalDeclaration.put("task3_start,task3_finish", "chan");
        return globalDeclaration;
    }

    public static DOTA buildComponent1_2_1() throws IOException {
        String base = ".\\src\\main\\resources\\experiments\\";
        String schedulePath = base + "experiment4\\schedule.json";
        String task1Path = base + "experiment4\\task1.json";
        DFA task1 = DFAUtil.getDFAFromJsonFile(task1Path);
        DOTA schedule = DOTAUtil.getDOTAFromJsonFile(schedulePath);
        schedule = DOTAUtil.getCartesian(schedule, task1);

        return schedule;

    }

    public static DOTA buildComponent1_2_2() throws IOException {
        String base = ".\\src\\main\\resources\\experiments\\";
        String schedulePath = base + "experiment4\\schedule.json";
        String task2Path = base + "experiment4\\task2.json";
        DFA task2 = DFAUtil.getDFAFromJsonFile(task2Path);
        DOTA schedule = DOTAUtil.getDOTAFromJsonFile(schedulePath);
        schedule = DOTAUtil.getCartesian(schedule, task2);

        return schedule;

    }

    public static DOTA buildComponent1_4() throws IOException {
        String base = ".\\src\\main\\resources\\experiments\\";
        String runnable1Path = base + "experiment4\\runnable1.json";
        String runnable2Path = base + "experiment4\\runnable2.json";
        String schedulePath = base + "experiment4\\schedule.json";
        String task1Path = base + "experiment4\\task1.json";
        DFA runnable1 = DFAUtil.getDFAFromJsonFile(runnable1Path);
        DFA runnable2 = DFAUtil.getDFAFromJsonFile(runnable2Path);
        DFA task1 = DFAUtil.getDFAFromJsonFile(task1Path);
        DOTA schedule = DOTAUtil.getDOTAFromJsonFile(schedulePath);

        schedule = DOTAUtil.getCartesian(schedule, runnable1);
        schedule = DOTAUtil.getCartesian(schedule, runnable2);
        schedule = DOTAUtil.getCartesian(schedule, task1);

        return schedule;

    }

    public static DOTA buildComponent1_4_2() throws IOException {
        String base = ".\\src\\main\\resources\\experiments\\";
        String schedulePath = base + "experiment4\\schedule.json";
        String task1Path = base + "experiment4\\task1.json";
        String task2Path = base + "experiment4\\task2.json";
        String task3Path = base + "experiment4\\task3.json";

        DFA task1 = DFAUtil.getDFAFromJsonFile(task1Path);
        DFA task2 = DFAUtil.getDFAFromJsonFile(task2Path);
        DFA task3 = DFAUtil.getDFAFromJsonFile(task3Path);
        DOTA schedule = DOTAUtil.getDOTAFromJsonFile(schedulePath);

        schedule = DOTAUtil.getCartesian(schedule, task1);
        schedule = DOTAUtil.getCartesian(schedule, task2);
        schedule = DOTAUtil.getCartesian(schedule, task3);

        return schedule;

    }

    public static DOTA buildComponent1_3() throws IOException {
        String base = ".\\src\\main\\resources\\experiments\\";
        String runnable2Path = base + "experiment4\\runnable2.json";
        String schedulePath = base + "experiment4\\schedule.json";
        String task1Path = base + "experiment4\\task1.json";
        DFA runnable2 = DFAUtil.getDFAFromJsonFile(runnable2Path);
        DFA task1 = DFAUtil.getDFAFromJsonFile(task1Path);
        DOTA schedule = DOTAUtil.getDOTAFromJsonFile(schedulePath);

//        schedule = DOTAUtil.getCartesian(schedule, runnable1);
        schedule = DOTAUtil.getCartesian(schedule, runnable2);
        schedule = DOTAUtil.getCartesian(schedule, task1);

        return schedule;
    }

    public static DOTA buildComponent1_3_2() throws IOException {
        String base = ".\\src\\main\\resources\\experiments\\";
        String schedulePath = base + "experiment4\\schedule.json";
        String task1Path = base + "experiment4\\task1.json";
        String task3Path = base + "experiment4\\task3.json";
        DFA task1 = DFAUtil.getDFAFromJsonFile(task1Path);
        DFA task3 = DFAUtil.getDFAFromJsonFile(task3Path);
        DOTA schedule = DOTAUtil.getDOTAFromJsonFile(schedulePath);
        schedule = DOTAUtil.getCartesian(schedule, task3);
        schedule = DOTAUtil.getCartesian(schedule, task1);

        return schedule;
    }
}












