package verification.frame.checkerimpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import ta.ota.DOTA;
import ta.ota.DOTAUtil;
import ta.ota.ResetLogicTimeWord;
import verification.Config;
import verification.frame.Checker;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.NTA;
import verification.uppaal.model.Template;
import verification.uppaal.verify.Result;
import verification.uppaal.verify.Verifyta;
import verification.util.VerificationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
public class Promise2Checker implements Checker<DOTA> {
    private List<Template> m2;
    private Map<String, Boolean> syncSendMap;
    private Declaration globalDeclaration;

    /**
     * check M2 \= assumption ?
     * 基于downSet重新补全
     * 两边移除不在downSet里的动作
     */
    public Result isSatisfied(DOTA assumption, Set<String> downSet) {
        //基于downSet重新生成hypothesis的sink状态
        assumption = VerificationUtil.removeSink(assumption);
        int beforeLocationCount = assumption.getLocations().size();
        assumption.setSigma(downSet);
        DOTAUtil.completeDOTA(assumption);
        if (assumption.getLocations().size() == beforeLocationCount) {
            return new Result(true, null, null);
        }
        // 忽略了的sigma
        Set<String> ignoreSigmas = syncSendMap.keySet().stream().filter(t -> !downSet.contains(t)).collect(Collectors.toSet());
        // assumption 消除无关动作

        if (Config.COMPLETED_EXAMPLE) {
            Template assumptionTemplate = VerificationUtil.transToUppaal(assumption, downSet, ignoreSigmas);
            VerificationUtil.refine(assumptionTemplate, syncSendMap, false);
            //创建性质，如果不会走到assumption的sink，即说明assumption包含m2
            String statement = "A[] not " + assumptionTemplate.getName() + "." + assumptionTemplate.getName() + "sink";
            return verify(m2, assumptionTemplate, null, statement, "nta");
        } else {
            Template assumptionTemplate = VerificationUtil.transToUppaal(assumption);
            VerificationUtil.refine(assumptionTemplate, syncSendMap, false);
            String statement = "A[] not " + assumptionTemplate.getName() + "." + assumptionTemplate.getName() + "sink";
            return verify(m2, assumptionTemplate, ignoreSigmas, statement, "nta");
        }
    }

    /**
     * check ctx \= M2
     */
    public Result isSatisfied(ResetLogicTimeWord ctx, Set<String> downSet) {
        DOTA assumption = VerificationUtil.traceOTA(ctx);
        int beforeLocationCount = assumption.getLocations().size();
        assumption.setSigma(downSet);
//        DOTAUtil.completeDOTA(assumption);
        Set<String> ignoreSigmas = syncSendMap.keySet().stream().filter(t -> !downSet.contains(t)).collect(Collectors.toSet());

        if (Config.COMPLETED_EXAMPLE) {
            Template assumptionTemplate = VerificationUtil.transToUppaal(assumption, downSet, ignoreSigmas);
            VerificationUtil.refine(assumptionTemplate, syncSendMap, false);
            String statement = "E<> " + assumptionTemplate.getName() + "." + assumption.getName() + beforeLocationCount;
            return verify(m2, assumptionTemplate, null, statement, "ctx");
        } else {
            Template assumptionTemplate = VerificationUtil.transToUppaal(assumption);
            VerificationUtil.refine(assumptionTemplate, syncSendMap, false);
            String statement = "E<> " + assumptionTemplate.getName() + "." + assumption.getName() + beforeLocationCount;
            return verify(m2, assumptionTemplate, ignoreSigmas, statement, "ctx");
        }


    }

    /**
     * 验证assumptionTemplate是否满足性质
     */
    @SneakyThrows
    private Result verify(List<Template> m2, Template assumptionTemplate, Set<String> ignoreSigmas, String statement, String ntaName) {
        //创建uppaal系统
        List<Template> templateList = new ArrayList<>(m2);
        templateList.add(assumptionTemplate);
        NTA nta = new NTA(ntaName, globalDeclaration, templateList, ignoreSigmas);
        //验证
        return Verifyta.isSatisfied(nta, statement);
    }

}
