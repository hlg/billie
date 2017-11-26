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
        String extractionResult = "";
        if(data instanceof TgBoQ && ((TgBoQ) data).getBoQInfo() != null)
            extractionResult = ((TgBoQ) data).getBoQInfo().getName();
        if (data instanceof TgBoQCtgy && ((TgBoQCtgy)data).getLblTx()!= null)
            extractionResult = getText(((TgBoQCtgy)data).getLblTx());
        if(data instanceof TgItem){
            String[] featureChain = new String[]{"description", "outlineText", "outlTxt", "txtOutlTxt"};
            EObject currentObject = data;
            for(String featureName : featureChain) {
                if(currentObject==null) break;
                currentObject = (EObject) getFeature(currentObject, featureName);
            }
            if(currentObject!=null){
                extractionResult =getTextFromMLList((EList<TgMLText>) currentObject);
            }
        }
        return (extractionResult.isEmpty()) ? getId() : extractionResult;

    }

    private String getTextFromMLList(EList<TgMLText> mlTexts) {
        StringBuilder result = new StringBuilder();
        for(TgMLText text: mlTexts){
            result.append(getText(text));
        }
        return result.toString();
    }

    private String getText(TgMLText txt){
        if(isListSet(txt.getDiv())) return getTextFromDivList(txt.getDiv());
        if(isListSet(txt.getBr())) return getTextFromStringList(txt.getBr());
        if(isListSet(txt.getSpan())) return getTextFromSpanList(txt.getSpan());
        if(isListSet(txt.getP())) return getTextFromPList(txt.getP());
        return "";
    }

    private boolean isListSet(EList eList){
        return eList!=null && !eList.isEmpty();
    }
    private String getTextFromPList(EList<TgpMLText> ps) {
        StringBuilder result = new StringBuilder();
        for(TgpMLText p : ps){
            result.append(getText(p));
        }
        return result.toString();
    }

    private String getText(TgpMLText p) {
        if(isListSet(p.getSpan()))return getTextFromSpanList(p.getSpan());
        if(isListSet(p.getBr())) return getTextFromStringList(p.getBr());
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
        if(isListSet(tgdiv.getSpan())) return getText(tgdiv);
        if(isListSet(tgdiv.getBr())) return getTextFromStringList(tgdiv.getBr());
        return "";
    }

    private Object getFeature(EObject object, String featureName){
        EStructuralFeature feature = object.eClass().getEStructuralFeature(featureName);
        return object.eGet(feature, true);
    }

    public String getId() {
        EStructuralFeature idFeature = data.eClass().getEStructuralFeature("iD");
        return (idFeature!=null) ? (String) data.eGet(idFeature, true) : null;
    }
}
