package fr.threedijnns.api.attributes;

import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.MaterialParameter;

public class Material {

	private int		ambientColor;
	private int		diffuseColor;
	private int		specularColor;
	private int		emissionColor;
	private float	shininess;

	public Material() {
		super();
	}

	public void setAmbient(int _color) { ambientColor = _color; }
	public void setDiffuse(int _color) { diffuseColor = _color; }
	public void setSpecular(int _color) { specularColor = _color; }
	public void setEmission(int _color) { emissionColor = _color; }

	public void setShininess(float _value) { shininess = _value; }

	public void enable() {
        gx.setMaterial(true);
        gx.setMaterial(FaceType.Front, MaterialParameter.AmbientColor, ambientColor);
        gx.setMaterial(FaceType.Front, MaterialParameter.DiffuseColor, diffuseColor);
        gx.setMaterial(FaceType.Front, MaterialParameter.SpecularColor, specularColor);
        gx.setMaterial(FaceType.Front, MaterialParameter.Shininess, shininess);
        //gxEngine.material(FaceType.Front, MaterialParameter.MAT_SHININESS, , m_Emission);
    }
	public void disable() {
        gx.setMaterial(false);
	}

}
