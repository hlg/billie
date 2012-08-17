package de.tudresden.cib.vis.runtime.java3d.colorTime;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

/**
 * @author helga
 */
public class ColorScale {
    private float b;
    private float g;
    private float r;
    private float t;


    ColorScale(float f, float g, float h) {
        this(f, g, h, Float.NaN);
    }

    ColorScale(float f, float g, float h, float transparency) {
        this.r = f;
        this.g = g;
        this.b = h;
        this.t = transparency;
    }

    Color3f createColor() {
        return new Color3f(r, g, b);
    }

    TransparencyAttributes createTransparency() {
        return (Float.isNaN(t) ? null : new TransparencyAttributes(TransparencyAttributes.NICEST, t));
    }

    public Appearance create() {
        Appearance appearance = new Appearance();
        Color3f color3f = createColor();
        Material material = new Material(color3f, new Color3f(0f, 0f, 0f), color3f, color3f, 10f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);
        TransparencyAttributes transparency = createTransparency();
        if (transparency != null) {
            appearance.setTransparencyAttributes(transparency);
        }
        return appearance;
    }
}
