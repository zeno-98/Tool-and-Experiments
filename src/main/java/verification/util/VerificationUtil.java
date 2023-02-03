package verification.util;


import ta.Clock;
import ta.TaLocation;
import ta.TaTransition;
import ta.TimeGuard;
import ta.ota.*;
import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;
import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.UppaalTransition;
import verification.uppaal.model.builder.TemplateBuilder;
import verification.uppaal.model.builder.UppaalLocationBuilder;
import verification.uppaal.model.builder.UppaalTransitionBuilder;

import java.util.*;
import java.util.stream.Collectors;

public final class VerificationUtil {

    /**
     * 基于逻辑重置时间字生产DOTA, 未补全，字母表自动生成
     */
    public static DOTA traceOTA(ResetLogicTimeWord resetLogicTimeWord) {
        Clock clock = new Clock("t");
        List<TaTransition> transitionList = new ArrayList<>();
        List<ResetLogicAction> actionList = resetLogicTimeWord.getTimedActions();
        List<TaLocation> locationList = new ArrayList<>();
        Set<String> sigma = new HashSet<>();
        for (int i = 1; i <= actionList.size() + 1; i++) {
            TaLocation location = new TaLocation(String.valueOf(i), String.valueOf(i), false, false);
            if (i == 1) {
                location.setInit(true);
            }
//            if (i == actionList.size()) {
//                location.setAccept(true);
//            }
            location.setAccept(true);
            locationList.add(location);
        }

        for (int i = 0; i < actionList.size(); i++) {
            ResetLogicAction resetLogicAction = actionList.get(i);
            String symbol = resetLogicAction.getSymbol();
            sigma.add(symbol);
            Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
            TimeGuard timeGuard = TimeGuard.bottomGuard(resetLogicAction.logicTimedAction());
            clockTimeGuardMap.put(clock, timeGuard);
            Set<Clock> clockSet = new HashSet<>();
            if (resetLogicAction.isReset()) {
                clockSet.add(clock);
            }
            TaTransition transition = new TaTransition(
                    locationList.get(i), locationList.get(i + 1), symbol, clockTimeGuardMap, clockSet);
            transitionList.add(transition);
        }
        return new DOTA("trace", sigma, locationList, transitionList, clock);
    }

    /**
     * 基于逻辑重置时间字生产DOTA, 基于给定字母表补全DOTA
     */
    public static DOTA traceOTA(ResetLogicTimeWord resetLogicTimeWord, Set<String> sigmas) {
        DOTA traceTA = traceOTA(resetLogicTimeWord);
        traceTA.setSigma(sigmas);
        for (TaTransition transition : traceTA.getTransitions()) {
            if (!sigmas.contains(transition.getSymbol())) {
                transition.setSymbol(null);
            }
        }
        DOTAUtil.completeDOTA(traceTA);
        return traceTA;
    }

    /**
     * 从dota生成uppaal自动机
     *
     * @param downSet      表示生产的uppaal自动机的动作集合，不在这个集合内的所有动作被删除
     * @param ignoreSigmas 为生成的uppaal自动机添加的无效自迁移动作的集合
     *                     ignoreSigmas和downSet的交集必须为空
     */
    public static Template transToUppaal(DOTA dota, Set<String> downSet, Set<String> ignoreSigmas) {
        Clock clock = dota.getClock();

        String name = dota.getName();

        Declaration localDeclaration = new Declaration();
        localDeclaration.put("x", "clock ");

        Map<TaLocation, UppaalLocation> locationMap = new HashMap<>();
        int transCount = ignoreSigmas == null ? 0 : dota.getLocations().size() * ignoreSigmas.size();
        List<UppaalTransition> selfIgnoreTransitions = new ArrayList<>(transCount);
        for (TaLocation location : dota.getLocations()) {
            String id = dota.getName() + location.getId();
            String name1 = dota.getName() + location.getName();
            UppaalLocation uppaalLocation = new UppaalLocationBuilder()
                    .setId(id)
                    .setName(name1)
                    .createLocation();
            locationMap.put(location, uppaalLocation);

            // 添加无效动作自迁移
            if (ignoreSigmas != null) {
                for (String ignoreSigma : ignoreSigmas) {
                    selfIgnoreTransitions.add(new UppaalTransitionBuilder(uppaalLocation, uppaalLocation)
                            .addSync(ignoreSigma)
                            .getUppaalTransition());
                }
            }
        }

        List<UppaalTransition> uppaalTransitionList = new ArrayList<>();
        for (TaTransition transition : dota.getTransitions()) {
            UppaalLocation source = locationMap.get(transition.getSourceLocation());
            UppaalLocation target = locationMap.get(transition.getTargetLocation());
            String sync = transition.getSymbol();
            if (!downSet.contains(sync)) {
                sync = null;
            }

            String assignment = transition.isReset(clock) ? "x:=0" : null;
            String guardText = pressTimeGuard(transition.getTimeGuard(clock));
            UppaalTransition uppaalTran = new UppaalTransitionBuilder(source, target)
                    .addSync(sync)
                    .addGuard(guardText)
                    .addAssignment(assignment)
                    .getUppaalTransition();
            uppaalTransitionList.add(uppaalTran);
        }

        uppaalTransitionList.addAll(selfIgnoreTransitions);
        List<UppaalLocation> uppaalLocationList = new ArrayList<>(locationMap.values());
        UppaalLocation location = locationMap.get(dota.getInitLocation());
        return new TemplateBuilder()
                .setName(name)
                .setLocalDeclaration(localDeclaration)
                .setUppaalLocationList(uppaalLocationList)
                .setUppaalTransitionList(uppaalTransitionList)
                .setInitUppaalLocation(location)
                .createTemplate();
    }

    public static Template transToUppaal(DOTA dota) {
        return transToUppaal(dota, dota.getSigma(), null);
    }

    private static String pressTimeGuard(TimeGuard timeGuard) {
        StringBuilder stringBuilder = new StringBuilder();
        if (timeGuard.isLowerBoundOpen()) {
            stringBuilder.append("x>").append(timeGuard.getLowerBound());
        } else {
            stringBuilder.append("x >=").append(timeGuard.getLowerBound());
        }

        if (timeGuard.getUpperBound() != TimeGuard.MAX_TIME) {
            stringBuilder.append(" && ");
            if (timeGuard.isUpperBoundOpen()) {
                stringBuilder.append("x<").append(timeGuard.getUpperBound());
            } else {
                stringBuilder.append("x<=").append(timeGuard.getUpperBound());
            }
        }

        return stringBuilder.toString();
    }

    public static void refine(Template template, Map<String, Boolean> syncSendMap, boolean flag) {
        List<UppaalTransition> transitions = template.getUppaalTransitionList().stream().peek(t -> {
            String symbol = t.getSynchronizedLabel().getText();
            if (syncSendMap.containsKey(symbol)) {
                String sync = (syncSendMap.get(symbol) == flag) ? "!" : "?";
                symbol = symbol + sync;
                t.getSynchronizedLabel().setText(symbol);
            } else {
                t.setSynchronizedLabel(null);
            }
        }).filter(t -> t.getSynchronizedLabel() != null || !t.getTarget().getName().equals("sink")).collect(Collectors.toList());
        template.setUppaalTransitionList(transitions);
    }

    public static DOTA removeSink(DOTA dota) {
        if (dota.getLocations().size() == 1) return dota;
        DOTA dota1 = new DOTA(dota.getName(),
                dota.getSigma(),
                dota.getLocations(),
                dota.getTransitions(),
                dota.getClock());
        TaLocation sink = null;
        List<TaLocation> locations = new ArrayList<>();
        for (TaLocation location : dota1.getLocations()) {
            if (!location.isAccept()) {
                sink = location;
            } else {
                locations.add(location);
            }
        }
        List<TaTransition> transitions = new ArrayList<>();
        for (TaTransition t : dota1.getTransitions()) {
            if (t.getTargetLocation() != sink) {
                transitions.add(t);
            }
        }
        dota1.setTransitions(transitions);
        dota1.setLocations(locations);
        return dota1;
    }

    public static ResetLogicTimeWord transferReset(LogicTimeWord logicTimeWord) {
        ResetLogicTimeWord resetLogicTimeWord = ResetLogicTimeWord.emptyWord();
        for (LogicTimedAction logicAction : logicTimeWord.getTimedActions()) {
            Boolean reset = Math.random() > 0.5;
            ResetLogicAction resetAction = new ResetLogicAction(logicAction.getSymbol(), logicAction.getValue(), reset);
            resetLogicTimeWord.concat(resetAction);
        }
        return resetLogicTimeWord;
    }

}
