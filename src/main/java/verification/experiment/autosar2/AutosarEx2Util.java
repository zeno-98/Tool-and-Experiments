package verification.experiment.autosar2;

import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;
import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.UppaalTransition;
import verification.uppaal.model.builder.TemplateBuilder;
import verification.uppaal.model.builder.UppaalLocationBuilder;
import verification.uppaal.model.builder.UppaalTransitionBuilder;
import verification.util.UppaalModelUtil;


public class AutosarEx2Util {

    public static Declaration getGlobalDeclaration() {
        Declaration globalDeclaration = new Declaration();
        globalDeclaration.put("read1,write1", "chan");
        globalDeclaration.put("read2,write2", "chan");
        globalDeclaration.put("read3,write3", "chan");
        globalDeclaration.put("read4,write4", "chan");
        globalDeclaration.put("read5,write5", "chan");
        globalDeclaration.put("runnable1_start", "chan");
        globalDeclaration.put("runnable2_start", "chan");
        globalDeclaration.put("runnable3_start", "chan");
        globalDeclaration.put("runnable4_start", "chan");
        globalDeclaration.put("runnable5_start", "chan");
        globalDeclaration.put("runnable6_start", "chan");
        globalDeclaration.put("task_start", "chan");
        globalDeclaration.put("overflow2", "chan");
        return globalDeclaration;
    }

    public static Template buildRunnable1() {
        String name = "runnable1";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implic_R");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active", "x<=4");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "write", "x<=4");
        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable1_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("write4", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addGuard("write1_count <= 2")
                .addSync("write1", "!")
                .addAssignment("write1_count++")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s3)
                .addSync("runnable2_start", "!")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s3, s1)
                .addGuard("x>=2&&write1_count>=2")
                .addAssignment("write1_count=0")
                .getUppaalTransition();

        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addLocalDeclaration("write1_count", "int")
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();

    }

    public static Template buildRunnable2() {
        String name = "runnable2";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implic_R");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active", "x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable2_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read1", "!")
                .getUppaalTransition();

        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s3)
                .addGuard("write2_count<=1")
                .addSync("write2", "!")
                .addAssignment("write2_count++")
                .getUppaalTransition();

        UppaalTransition t4 = new UppaalTransitionBuilder(s3, s1)
                .addSync("runnable3_start", "!")
                .addGuard("write2_count>=1")
                .addAssignment("write2_count=0")
                .getUppaalTransition();

        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addLocalDeclaration("write2_count", "int")
                .addInitLocation(s1)
                .addLocations(s2, s3)
                .addTransitions(t1, t2, t3, t4)
                .createTemplate();
    }

    public static Template buildRunnable3() {
        String name = "runnable3";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implic_R");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active", "x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable3_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read2", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s1)
                .getUppaalTransition();

        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3)
                .addTransitions(t1, t2, t3)
                .createTemplate();

    }

    public static Template buildRunnable4() {
        String name = "runnable4";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implic_R");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active", "x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable4_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read4", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s1)
                .addSync("write3", "!")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3)
                .addTransitions(t1, t2, t3)
                .createTemplate();

        return template;
    }

    public static Template buildRunnable5() {
        String name = "runnable5";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implic_R");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active", "x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable5_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("write5", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s1)
                .addSync("read3", "!")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3)
                .addTransitions(t1, t2, t3)
                .createTemplate();

        return template;
    }

    public static Template buildRunnable6() {
        String name = "runnable6";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "passive");
        UppaalLocation s2 = UppaalModelUtil.buildCommittedUppaalLocation(name, "implic_R");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "active", "x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable6_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read5", "!")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s1)
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3)
                .addTransitions(t1, t2, t3)
                .createTemplate();

        return template;
    }

    public static Template buildBuffer1() {
        String name = "buffer1";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count<=len")
                .addSync("write1", "?")
                .addAssignment("count++")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count>=0")
                .addSync("read1", "?")
                .addAssignment("count--")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=1", "int")
                .addInitLocation(s1)
                .addTransitions(t1, t2)
                .createTemplate();
        return template;
    }

    public static Template buildBuffer2() {
        String name = "buffer2";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count<len")
                .addSync("write2", "?")
                .addAssignment("count++")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count>=0")
                .addSync("read2", "?")
                .addAssignment("count--")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count == len")
                .addSync("overflow2", "!")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=3000", "int")
                .addInitLocation(s1)
                .addTransitions(t1, t2, t3)
                .createTemplate();
        return template;
    }

    public static Template buildBuffer3() {
        String name = "buffer3";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count<=len")
                .addSync("write3", "?")
                .addAssignment("count++")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count>=0")
                .addSync("read3", "?")
                .addAssignment("count--")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=3", "int")
                .addInitLocation(s1)
                .addTransitions(t1, t2)
                .createTemplate();
        return template;
    }

    public static Template buildBuffer4() {
        String name = "buffer4";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addSync("write4", "?")
                .addAssignment("count=count%len+1")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count>=0")
                .addSync("read4", "?")
                .addAssignment("count--")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=3", "int")
                .addInitLocation(s1)
                .addTransitions(t1, t2)
                .createTemplate();
        return template;
    }

    public static Template buildBuffer5() {
        String name = "buffer5";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "initial");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count<=len")
                .addSync("write5", "?")
                .addAssignment("count++")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count>=0")
                .addSync("read5", "?")
                .addAssignment("count--")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=3", "int")
                .addInitLocation(s1)
                .addTransitions(t1, t2)
                .createTemplate();
        return template;
    }

    public static Template buildTask() {
        String name = "task";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=1");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("task_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addGuard("x<=1")
                .addSync("runnable4_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addSync("runnable5_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s4, s1)
                .addSync("runnable6_start", "!")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4)
                .addLocalDeclaration("x", "clock")
                .createTemplate();

        return template;
    }

    public static Template buildRTE() {
        String name = "rte";

        UppaalLocation initial = new UppaalLocationBuilder()
                .setName("initial")
                .setId(name + "init")
                .createLocation();

        UppaalLocation l1 = new UppaalLocationBuilder()
                .setName("timeLimit")
                .setId(name + "time")
                .createLocation();

        UppaalLocation l2 = new UppaalLocationBuilder()
                .setName("NotTimeLimit")
                .setId(name + "nolimit")
                .createLocation();

        UppaalTransition t1 = new UppaalTransitionBuilder(initial, l1)
                .addSync("overflow2", "?")
                .addGuard("x >= 100")
                .getUppaalTransition();

        UppaalTransition t2 = new UppaalTransitionBuilder(l1, l1)
                .addSync("read2", "!")
                .addGuard("count <= 5")
                .addAssignment("count ++")
                .getUppaalTransition();

        UppaalTransition t3 = new UppaalTransitionBuilder(l1, initial)
                .addAssignment("count = 0")
                .appendAssignment("x = 0")
                .addGuard("count > 5")
                .getUppaalTransition();

        UppaalTransition t4 = new UppaalTransitionBuilder(initial, l2)
                .addSync("overflow2", "?")
                .addGuard("x < 100")
                .getUppaalTransition();

        UppaalTransition t5 = new UppaalTransitionBuilder(l2, l2)
                .addSync("read2", "!")
                .addGuard("count <= 10")
                .addAssignment("count ++")
                .getUppaalTransition();

        UppaalTransition t6 = new UppaalTransitionBuilder(l2, initial)
                .addGuard("count > 10")
                .addAssignment("count = 0")
                .appendAssignment("x = 0")
                .getUppaalTransition();

        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addLocalDeclaration("count", "int")
                .addInitLocation(initial)
                .addLocations(l1, l2)
                .addTransitions(t1, t2, t3, t4, t5, t6)
                .createTemplate();
    }

    public static Template buildSchedule() {
        String name = "schedule";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1", "x<=5");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=10");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3", "x<=10");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4", "x<=10");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("x<=5")
                .addSync("runnable1_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addGuard("x<=10")
                .addSync("runnable1_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addGuard("x<=5")
                .addSync("runnable1_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s2, s1)
                .addGuard("x==10")
                .addSync("task_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s3, s1)
                .addGuard("x==10")
                .addSync("task_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t6 = new UppaalTransitionBuilder(s4, s1)
                .addGuard("x==10")
                .addSync("task_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4, t5, t6)
                .createTemplate();
    }

}
