package verification.uppaal.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import verification.uppaal.constants.UppaalConstants;
import verification.uppaal.model.label.SynchronizedLabel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
public class NTA {
    private String name = "nta";
    private Declaration globalDeclaration;
    private List<Template> templateList;
    private Set<String> ignoreSigma;

    public NTA(Declaration globalDeclaration, List<Template> templateList) {
        this.globalDeclaration = globalDeclaration;
        this.templateList = templateList;
    }

    public NTA(String name, Declaration globalDeclaration, List<Template> templateList) {
        this.name = name;
        this.globalDeclaration = globalDeclaration;
        this.templateList = templateList;
    }

    public Template getTemplate(String name) {
        for (Template template : templateList) {
            if (template.getName().equals(name)) {
                return template;
            }
        }
        return null;
    }

    public void writeToUppaalXml(String path) throws IOException {
        Document document = DocumentFactory.getInstance().createDocument();

        document.addDocType(UppaalConstants.NTA, UppaalConstants.DOC_TYPE_S1, UppaalConstants.DOC_TYPE_S2);

        Element root = document.addElement(UppaalConstants.NTA);
        Element declaration = root.addElement(UppaalConstants.DECLARATION);
        declaration.addText(getGlobalDeclaration().toString());
        StringBuilder systemBuilder = new StringBuilder();
        systemBuilder.append("system ");

        for (Template template : getTemplateList()) {
            systemBuilder.append(template.getName()).append(",");

            Element templateElement = root.addElement(UppaalConstants.TEMPLATE);
            Element name = templateElement.addElement(UppaalConstants.NAME);
            name.addText(template.getName());

            if (template.getParameter() != null) {
                Element parameter = templateElement.addElement(UppaalConstants.PARAMETER);
                parameter.addText(template.getParameter());
            }

            Element templateDeclaration = templateElement.addElement(UppaalConstants.DECLARATION);
            if (template.getLocalDeclaration() != null && template.getLocalDeclaration().isMapInitialized()) {
                templateDeclaration.addText(template.getLocalDeclaration().toString());
            }

            for (UppaalLocation uppaalLocation : template.getUppaalLocationList()) {
                Element location = templateElement.addElement(UppaalConstants.LOCATION);
                location.addAttribute(UppaalConstants.ID, template.getName() + "_" + uppaalLocation.getId());
                Element locationName = location.addElement(UppaalConstants.NAME);
                if (uppaalLocation.isUrgent()) {
                    location.addElement(UppaalConstants.URGENT);
                }
                if (uppaalLocation.isCommitted()) {
                    location.addElement((UppaalConstants.COMMITTED));
                }
                locationName.addText(uppaalLocation.getName());
                if (uppaalLocation.getInvariantLabel() != null) {
                    Element label = location.addElement(UppaalConstants.LABEL);
                    label.addText(uppaalLocation.getInvariantLabel().getText());
                    label.addAttribute("kind", "invariant");
                }
            }

            Element init = templateElement.addElement(UppaalConstants.INIT);
            init.addAttribute(UppaalConstants.REF, template.getName() + "_" + template.getInitUppaalLocation().getId());

            List<UppaalTransition> uppaalUppaalTransitionList = template.getUppaalTransitionList();
            for (UppaalTransition uppaalTransition : uppaalUppaalTransitionList) {
                Element transition = templateElement.addElement(UppaalConstants.TRANSITION);

                Element source = transition.addElement(UppaalConstants.SOURCE);
                source.addAttribute(UppaalConstants.REF, template.getName() + "_" + uppaalTransition.getSource().getId());

                Element target = transition.addElement(UppaalConstants.TARGET);
                target.addAttribute(UppaalConstants.REF, template.getName() + "_" + uppaalTransition.getTarget().getId());

                if (uppaalTransition.getSelectLabel() != null) {
                    Element selectLabel = transition.addElement(UppaalConstants.LABEL);
                    selectLabel.addAttribute(UppaalConstants.KIND, UppaalConstants.SELECT);
                    selectLabel.addText(uppaalTransition.getSelectLabel().getText());
                }

                SynchronizedLabel syncLable = uppaalTransition.getSynchronizedLabel();
                if (checkIgnore(syncLable)) {
                    Element sycnLabel = transition.addElement(UppaalConstants.LABEL);
                    sycnLabel.addAttribute(UppaalConstants.KIND, UppaalConstants.SYNCHRONISATION);
                    sycnLabel.addText(syncLable.getText());
                }

                if (uppaalTransition.getGuardLabel() != null
                        && null != uppaalTransition.getGuardLabel().getText()) {
                    Element guardLabel = transition.addElement(UppaalConstants.LABEL);
                    guardLabel.addAttribute(UppaalConstants.KIND, UppaalConstants.GUARD);
                    guardLabel.addText(uppaalTransition.getGuardLabel().getText());

                }
                if (uppaalTransition.getAssignmentLabel() != null
                        && null != uppaalTransition.getAssignmentLabel().getText()) {
                    Element assignmentLabel = transition.addElement(UppaalConstants.LABEL);
                    assignmentLabel.addAttribute(UppaalConstants.KIND, UppaalConstants.ASSIGNMENT);
                    assignmentLabel.addText(uppaalTransition.getAssignmentLabel().getText());
                }
            }
        }
        systemBuilder.deleteCharAt(systemBuilder.length() - 1);
        systemBuilder.append(";");

        Element system = root.addElement(UppaalConstants.SYSTEM);
        system.addText(systemBuilder.toString());

        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setTrimText(false);

        XMLWriter writer = new XMLWriter(new FileOutputStream(path), outputFormat);

        writer.write(document);
        writer.close();

    }

    /**
     * 校验同步信号是否需要构建
     */
    private boolean checkIgnore(SynchronizedLabel syncLable) {
        if (syncLable == null) {
            return false;
        }
        if (ignoreSigma == null) {
            return true;
        }
        String labelText = syncLable.getText();
        return !ignoreSigma.contains(labelText.substring(0, labelText.length() - 1));
    }
}
