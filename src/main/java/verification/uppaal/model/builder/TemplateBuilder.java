package verification.uppaal.model.builder;


import verification.uppaal.model.Declaration;
import verification.uppaal.model.Template;
import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.UppaalTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateBuilder {
    private String name;
    private String parameter;
    private Declaration localDeclaration;
    private List<UppaalLocation> uppaalLocationList;
    private List<UppaalTransition> uppaalTransitionList;
    private UppaalLocation initUppaalLocation;

    public TemplateBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TemplateBuilder addLocalDeclaration(String key, String value){
        if (localDeclaration == null){
            localDeclaration = new Declaration();
        }
        Map<String, String> map = localDeclaration.getMap();
        map.put(key, value);
        return this;
    }

    public TemplateBuilder addLocation(UppaalLocation uppaalLocation){
        if (uppaalLocationList == null){
            uppaalLocationList = new ArrayList<>();
        }
        uppaalLocationList.add(uppaalLocation);
        return this;
    }

    public TemplateBuilder addLocations(UppaalLocation... uppaalLocations){
        for(UppaalLocation uppaalLocation : uppaalLocations){
            addLocation(uppaalLocation);
        }
        return this;
    }

    public TemplateBuilder addInitLocation(UppaalLocation initUppaalLocation){
        this.initUppaalLocation = initUppaalLocation;
        return addLocation(initUppaalLocation);
    }

    public TemplateBuilder addTransition(UppaalTransition uppaalTransition){
        if (uppaalTransitionList == null){
            uppaalTransitionList = new ArrayList<>();
        }
        uppaalTransitionList.add(uppaalTransition);
        return this;
    }

    public TemplateBuilder addTransitions(UppaalTransition... uppaalTransitions){
        for (UppaalTransition uppaalTransition : uppaalTransitions){
            addTransition(uppaalTransition);
        }
        return this;
    }

    public Template createTemplate() {
        return new Template(name,parameter, localDeclaration, uppaalLocationList, uppaalTransitionList, initUppaalLocation);
    }

    public TemplateBuilder setLocalDeclaration(Declaration localDeclaration) {
        this.localDeclaration = localDeclaration;
        return this;
    }

    public TemplateBuilder setUppaalLocationList(List<UppaalLocation> uppaalUppaalLocationList) {
        this.uppaalLocationList = uppaalUppaalLocationList;
        return this;
    }

    public TemplateBuilder setUppaalTransitionList(List<UppaalTransition> uppaalTransitionList) {
        this.uppaalTransitionList = uppaalTransitionList;
        return this;
    }

    public TemplateBuilder setInitUppaalLocation(UppaalLocation uppaalLocation) {
        this.initUppaalLocation = uppaalLocation;
        return this;
    }

    public TemplateBuilder setParameter(String text){
        this.parameter = text;
        return this;
    }
}