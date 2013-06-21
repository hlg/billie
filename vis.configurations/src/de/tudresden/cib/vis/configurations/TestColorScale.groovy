package de.tudresden.cib.vis.configurations

import cib.lib.gaeb.model.gaeb.TgItem
import cib.mf.qto.model.AnsatzType
import de.tudresden.cib.vis.configurations.IfcGaeb_Colored3D.ColorScale
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser
import de.tudresden.cib.vis.data.multimodel.LinkedObject
import org.bimserver.models.ifc2x3tc1.*

public class TestColorScale implements ColorScale {
    // for IfcGaeb_Colored3D configuration

    public String gaebX84Id = "M1";
	public boolean absolute = false;
	public boolean adjustWalls = false;
	public double wallPriceFactor = 25;
	
	public double calculateValue(LinkedObject<EMFIfcParser.EngineEObject> element) {
		double price = calculateOveralPrice(element.getResolvedLinks()).doubleValue();
		double volume = absolute ? 1 : extractVolume(element.getResolvedLinks()); // extractVolume(element.getKeyObject());
		return (volume > 0) ? (price / volume) : 0;
	}
	private double extractVolume(EMFIfcParser.EngineEObject keyObject) {
		for(IfcRelDefines rel : ((IfcProduct) keyObject.getObject()).getIsDefinedBy()){
			if(rel instanceof IfcRelDefinesByProperties && ((IfcRelDefinesByProperties) rel).getRelatingPropertyDefinition() instanceof IfcElementQuantity){
				for(IfcPhysicalQuantity quantity :((IfcElementQuantity) ((IfcRelDefinesByProperties) rel).getRelatingPropertyDefinition()).getQuantities()){
					if(quantity.getName().equals("GrossVolume") && quantity instanceof IfcQuantityVolume) return ((IfcQuantityVolume) quantity).getVolumeValue();
				}
			}
		}
		return 1;
	}
	private double extractVolume(Collection<LinkedObject.ResolvedLink> resolvedLinks) {
		for(LinkedObject.ResolvedLink resolvedLink: resolvedLinks){
			if("m3".equals(resolvedLink.getLinkedBoQ().get(gaebX84Id).getQU())) return resolvedLink.getLinkedQto().values().iterator().next().getResult();
		}
		return 0;
	}
	private BigDecimal calculateOveralPrice(Collection<LinkedObject.ResolvedLink> resolvedLinks) {
		BigDecimal price = new BigDecimal(0);
		for (LinkedObject.ResolvedLink link : resolvedLinks) {
			if (!link.getLinkedBoQ().isEmpty() && !link.getLinkedQto().isEmpty()) {
				TgItem gaebAngebot = link.getLinkedBoQ().get(gaebX84Id);
				AnsatzType qto = link.getLinkedQto().values().iterator().next();
				def txt = gaebAngebot.description.completeText?.outlineText?.outlTxt?.textOutlTxt?.p?.span?.value?.flatten()
				double factor = (adjustWalls && txt?.any{it.contains('Innenwände')} 
					? wallPriceFactor 
					: (adjustWalls && txt?.any{it.contains('Decken')} ? 1/wallPriceFactor : 1))
				price = price.add(gaebAngebot.getUP().multiply(BigDecimal.valueOf(qto.getResult()))*factor);
			}
		}
		return price;
	}
}
