package verification.experiment.autosar1;

import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;
import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.UppaalTransition;
import verification.uppaal.model.builder.TemplateBuilder;
import verification.uppaal.model.builder.UppaalLocationBuilder;
import verification.uppaal.model.builder.UppaalTransitionBuilder;
import verification.util.UppaalModelUtil;


public class AutosarEx1Util {

    public static Declaration getGlobalDeclaration() {
        Declaration globalDeclaration = new Declaration();
        globalDeclaration.put("read1,write1", "chan");
        globalDeclaration.put("read2,write2", "chan");
        globalDeclaration.put("runnable1_start", "chan");
        globalDeclaration.put("runnable2_start", "chan");
        globalDeclaration.put("runnable3_start", "chan");
        globalDeclaration.put("runnable4_start", "chan");
        return globalDeclaration;
    }

    public static Template buildRunnable1() {
        String name = "runnable1";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=4");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3", "x<=4");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4", "x>=2&&x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable1_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s4)
                .addSync("write1", "!")
                .addGuard("x<=4")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s2, s3)
                .addSync("write1", "!")
                .addGuard("x<=4")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s3, s4)
                .addSync("write1", "!")
                .addGuard("x<=4")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s4, s1)
                .addGuard("x>=2")
                .getUppaalTransition();

        return new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();

    }

    public static Template buildRunnable2() {
        String name = "runnable2";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = new UppaalLocationBuilder()
                .setName("s2")
                .setId(name + "s2")
                .setInvariantLabel("x<=4")
                .createLocation();

        UppaalLocation s3 = new UppaalLocationBuilder()
                .setName("s3")
                .setId(name + "s3")
                .setInvariantLabel("x>=2&&x<=4")
                .createLocation();

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable2_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read1", "!")
                .addGuard("x<=4")
                .getUppaalTransition();

        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s1)
                .addGuard("x>=2")
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

    public static Template buildRunnable3() {
        String name = "runnable3";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=4");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3", "x<=4");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4", "x>=2&&x<=4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable3_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s4)
                .addSync("write2", "!")
                .addGuard("x<=4")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s2, s3)
                .addSync("write2", "!")
                .addGuard("x<=4")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s3, s4)
                .addSync("write2", "!")
                .addGuard("x<=4")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s4, s1)
                .addGuard("x>=2")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("x", "clock")
                .addInitLocation(s1)
                .addLocations(s2, s3, s4)
                .addTransitions(t1, t2, t3, t4, t5)
                .createTemplate();

        return template;

    }

    public static Template buildRunnable4() {
        String name = "runnable4";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = new UppaalLocationBuilder()
                .setName("s2")
                .setId(name + "s2")
                .setInvariantLabel("x<=4")
                .createLocation();

        UppaalLocation s3 = new UppaalLocationBuilder()
                .setName("s3")
                .setId(name + "s3")
                .setInvariantLabel("x>=2&&x<=4")
                .createLocation();

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addSync("runnable4_start", "?")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addSync("read2", "!")
                .addGuard("x<=4")
                .getUppaalTransition();

        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s1)
                .addGuard("x>=2")
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

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation error = UppaalModelUtil.buildUppaalLocation(name, "error");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count<len")
                .addSync("write1", "?")
                .addAssignment("count++")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count>=0")
                .addSync("read1", "?")
                .addAssignment("count--")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s1, s1)
                .addGuard("count > len")
                .addAssignment("count=len/2")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s1, error)
                .addGuard("count < 0")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=30", "int")
                .addInitLocation(s1)
                .addLocations(error)
                .addTransitions(t1, t2, t3, t4)
                .createTemplate();
        return template;
    }

    public static Template buildBuffer2() {
        String name = "buffer2";

        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation error = UppaalModelUtil.buildUppaalLocation(name, "error");

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
                .addGuard("count > len")
                .addAssignment("count=len/2")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s1, error)
                .addGuard("count < 0")
                .getUppaalTransition();

        Template template = new TemplateBuilder()
                .setName(name)
                .addLocalDeclaration("count", "int")
                .addLocalDeclaration("len=30", "int")
                .addInitLocation(s1)
                .addLocations(error)
                .addTransitions(t1, t2, t3, t4)
                .createTemplate();
        return template;
    }

    public static Template buildSchedule1() {
        String name = "schedule1";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1", "x<=1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=10");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3", "x<=10");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4", "x<=10");
//        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
//        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2");
//        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3");
//        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("x==1")
                .addSync("runnable1_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s2, s1)
                .addGuard("x==10")
                .addSync("runnable2_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addGuard("x==2")
                .addSync("runnable1_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s3, s1)
                .addGuard("x==10")
                .addSync("runnable2_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addGuard("x==3")
                .addSync("runnable1_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t6 = new UppaalTransitionBuilder(s4, s1)
                .addGuard("x==10")
                .addSync("runnable2_start", "!")
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

    public static Template buildSchedule2() {
        String name = "schedule2";
        UppaalLocation s1 = UppaalModelUtil.buildUppaalLocation(name, "s1");
        UppaalLocation s2 = UppaalModelUtil.buildUppaalLocation(name, "s2", "x<=20");
        UppaalLocation s3 = UppaalModelUtil.buildUppaalLocation(name, "s3", "x<=20");
        UppaalLocation s4 = UppaalModelUtil.buildUppaalLocation(name, "s4", "x<=20");

        UppaalTransition t1 = new UppaalTransitionBuilder(s1, s2)
                .addGuard("x==5")
                .addSync("runnable3_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t2 = new UppaalTransitionBuilder(s2, s3)
                .addGuard("x==6")
                .addSync("runnable3_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t3 = new UppaalTransitionBuilder(s3, s4)
                .addGuard("x==8")
                .addSync("runnable3_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t4 = new UppaalTransitionBuilder(s2, s1)
                .addGuard("x==20")
                .addSync("runnable4_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t5 = new UppaalTransitionBuilder(s3, s1)
                .addGuard("x==20")
                .addSync("runnable4_start", "!")
                .addAssignment("x=0")
                .getUppaalTransition();
        UppaalTransition t6 = new UppaalTransitionBuilder(s4, s1)
                .addGuard("x==20")
                .addSync("runnable4_start", "!")
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
