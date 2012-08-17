package de.tudresden.cib.vis.runtime.java3d.colorTime;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

public enum TypeAppearance {

    IfcWindowImpl(0f, 0f, 1f, 0.5f),
    IfcWallImpl(0.537255f, 0.537255f, 0.537255f),
    IfcOpeningElementImpl(1f, 1f, 1f),
    IfcSlabImpl(0.137255f, 0.137255f, 0.170588f),
    IfcRoofImpl(1f, 0f, 0f),
    IfcColumnImpl(0.437255f, 0.603922f, 0.370588f, 0.5f),
    IfcSpaceImpl(0.137255f, 0.403922f, 0.870588f),
    IfcDoorImpl(0.637255f, 0.603922f, 0.670588f),
    IfcRailingImpl(0.137255f, 0.203922f, 0.270588f),
    IfcFurnishingElementImpl(0.437255f, 0.603922f, 0.370588f),
    IfcStairImpl(0.137255f, 0.137255f, 0.170588f),
    IfcBeamImpl(0.437255f, 0.603922f, 0.370588f),
    IfcFlowTerminalImpl(0.437255f, 0.603922f, 0.370588f),
    IfcDistributionFlowElementImpl(0.437255f, 0.603922f, 0.370588f),
    IfcSiteImpl(0f, 0.5f, 0f),
    IfcProxyImpl(0.137255f, 0.137255f, 0.170588f),
    DEFAULT(0.5f, 0.5f, 0.5f),
    OFF(0.5f, 0.5f, 0.5f, 1),
    INACTIVE(0.5f, 0.5f, 0.5f, 0.8f),
    ACTIVATED(1f, 0, 0),
    DEACTIVATED(0, 0.3f, 0, 0.6f);

    private float b;
    private float g;
    private float r;
    private float t;
    private Appearance appearance = null;

    TypeAppearance(float f, float g, float h) {
        this(f, g, h, Float.NaN);
    }

    TypeAppearance(float f, float g, float h, float transparency) {
        this.r = f;
        this.g = g;
        this.b = h;
        this.t = transparency;
    }

    Color3f createColor() {
        return new Color3f(r, g, b);
    }

    public Appearance getAppearance() {
        if (appearance == null) appearance = createAppearance();
        return appearance;
    }

    TransparencyAttributes createTransparency() {
        return (Float.isNaN(t) ? null : new TransparencyAttributes(TransparencyAttributes.NICEST, t));
    }

    public Appearance createAppearance() {
        Appearance appearance = new Appearance();
        Color3f color3f = createColor();
        Material material = new Material(color3f, new Color3f(0f, 0f, 0f), color3f, color3f, 10f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);
        PolygonAttributes polygonAttributes= new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE); // draw correctly even with flipped normals
        appearance.setPolygonAttributes(polygonAttributes);
        TransparencyAttributes transparency = createTransparency();
        if (transparency != null) {
            appearance.setTransparencyAttributes(transparency);
        }
        return appearance;
    }

}