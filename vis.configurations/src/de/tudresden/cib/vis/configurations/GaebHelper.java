package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class GaebHelper {

    private final EObject data;
    private String id;

    public GaebHelper(EObject data){
        this.data = data;
    }

    public String safeExtractText(){

        String[] featureChain =
                data instanceof TgItem ? new String[]{"description", "outlineText", "outlTxt", "txtOutlTxt"} :
                data instanceof TgBoQCtgy ? new String[]{"lblTxt"} :
                data instanceof TgBoQ ? new String[]{"boQInfo","name"}: null;
        String extractionResult = "";
        if(featureChain!=null){
            EObject currentObject = data;
            for(String featureName : featureChain) {
                currentObject = (EObject) getFeature(currentObject, featureName);
                if(currentObject==null) break;
            }
            if(currentObject!=null){
                extractionResult = getTextFromMLList((EList<TgMLText>) currentObject);
            }
        }
        return (extractionResult.isEmpty()) ? getId() + " - without outline text" : extractionResult;

    }

    private String getTextFromMLList(EList<TgMLText> mlTexts) {
        StringBuilder result = new StringBuilder();
        for(TgMLText text: mlTexts){
            result.append(getText(text));
        }
        return result.toString();
    }

    private String getText(TgMLText txt){
        if(txt.getDiv()!=null) return getTextFromDivList(txt.getDiv());
        if(txt.getBr()!=null) return getTextFromStringList(txt.getBr());
        if(txt.getSpan()!=null) return getTextFromSpanList(txt.getSpan());
        if(txt.getP()!=null) return getTextFromPList(txt.getP());
        return "";
    }

    private String getTextFromPList(EList<TgpMLText> ps) {
        StringBuilder result = new StringBuilder();
        for(TgpMLText p : ps){
            result.append(getText(p));
        }
        return result.toString();
    }

    private String getText(TgpMLText p) {
        if(p.getSpan()!=null)return getTextFromSpanList(p.getSpan());
        if(p.getBr()!=null) return getTextFromStringList(p.getBr());
        return "";
    }

    private String getTextFromSpanList(EList<Tgspan> spans) {
        StringBuilder result = new StringBuilder();
        for(Tgspan span: spans){
            result.append(getText(span));
        }
        return result.toString();
    }

    private String getText(Tgspan span) {
        return span.getValue();
    }

    private String getTextFromStringList(EList<String> br) {
        StringBuilder result = new StringBuilder();
        for ( String para : br){
            result.append(para);
        }
        return result.toString();
    }

    private String getTextFromDivList(EList<Tgdiv> list) {
        StringBuilder result = new StringBuilder();
        for (Tgdiv element : list){
            result.append(getText(element));
        }
        return result.toString();
    }

    private String getText(Tgdiv tgdiv) {
        if(tgdiv.getSpan()!=null) return getText(tgdiv);
        if(tgdiv.getBr()!=null) return getTextFromStringList(tgdiv.getBr());
        return "";
    }

    private Object getFeature(EObject object, String featureName){
        EStructuralFeature feature = object.eClass().getEStructuralFeature(featureName);
        return object.eGet(feature, true);
    }

    public String getId() {
        EStructuralFeature idFeature = data.eClass().getEStructuralFeature("ID");
        return (idFeature!=null) ? (String) data.eGet(idFeature, true) : null;
    }
}
