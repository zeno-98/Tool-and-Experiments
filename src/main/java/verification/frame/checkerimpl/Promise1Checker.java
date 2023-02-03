package verification.frame.checkerimpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import ta.ota.DOTA;
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

/**
 * 该类用于判断假设保证推理中的第一条保证是否满足
 */
@Data
@AllArgsConstructor
public class Promise1Checker implements Checker<DOTA> {

    //uppaal的全局声明
    private Declaration globalDeclaration;
    //组合验证中的M1自动机
    private List<Template> m1;
    //组合验证中的安全性性质
    private String statement;
    //信号的方向映射
    private Map<String, Boolean> syncSendMap;

    public Result isSatisfied(DOTA assumption, Set<String> downSet) {
        return isSatisfied(assumption, downSet, "nta");
    }

    public Result isSatisfied(ResetLogicTimeWord ctx, Set<String> downSet) {
        DOTA counterexample = VerificationUtil.traceOTA(ctx);
        return isSatisfied(counterexample, downSet, "ctx");
    }

    private Result isSatisfied(DOTA assumption, Set<String> downSet, String ntaName) {
        assumption = VerificationUtil.removeSink(assumption);
        assumption.setSigma(downSet);
        // 忽略了的sigma
        Set<String> ignoreSigmas = syncSendMap.keySet().stream().filter(t -> !downSet.contains(t)).collect(Collectors.toSet());
        if (Config.COMPLETED_EXAMPLE) {
            Template assumptionTemplate = VerificationUtil.transToUppaal(assumption, downSet, ignoreSigmas);
            VerificationUtil.refine(assumptionTemplate, syncSendMap, true);
            return verify(m1, assumptionTemplate, null, ntaName);
        } else {
            Template assumptionTemplate = VerificationUtil.transToUppaal(assumption);
            VerificationUtil.refine(assumptionTemplate, syncSendMap, true);
            return verify(m1, assumptionTemplate, ignoreSigmas, ntaName);
        }
    }

    public Result isSatisfied(DOTA assumption) {
        assumption = VerificationUtil.removeSink(assumption);
        Template assumptionTemplate = VerificationUtil.transToUppaal(assumption, syncSendMap.keySet(), null);
        VerificationUtil.refine(assumptionTemplate, syncSendMap, true);
        return verify(m1, assumptionTemplate, null, "nta");
    }

    /**
     * 验证assumptionTemplate是否满足性质
     */
    @SneakyThrows
    private Result verify(List<Template> m1, Template assumptionTemplate, Set<String> ignoreSigmas, String ntaName) {
        //创建uppaal系统
        List<Template> templateList = new ArrayList<>(m1);
        templateList.add(assumptionTemplate);
        NTA nta = new NTA(ntaName, globalDeclaration, templateList, ignoreSigmas);
        return Verifyta.isSatisfied(nta, statement);
    }
}
