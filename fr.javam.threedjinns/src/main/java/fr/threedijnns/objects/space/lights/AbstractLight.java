package fr.threedijnns.objects.space.lights;

import fr.java.lang.properties.ID;
import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.space.Point3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.GxParent;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.interfaces.nodes.space.GxLight;
import fr.threedijnns.api.lang.enums.LightParameter;
import fr.threedijnns.api.lang.enums.TLightID;
import fr.threedijnns.api.lang.enums.TLightMode;

public class AbstractLight implements GxLight, GxRenderable {
	static protected Integer m_GlobalAmbient;

	protected ID			id;

	protected TLightMode	mode;
	protected TLightID		lightID;

	protected long 			m_StatusLight;

	protected Integer		ambientColor;
	protected Integer		diffuseColor;
	protected Integer		specularColor;

	protected Point3D		m_Position;
	protected Vector3D		m_Direction;

	protected float			m_Exposant;
	protected float			m_ConeAngle;

	protected float			m_ConstantAttenuation;
	protected float			m_LinearAttenuation;
	protected float			m_QuadraticAttenuation;

	public AbstractLight(TLightID _id) {
		super();
	}

	static void setGlobalAmbientLight(byte _r, byte _g, byte _b, byte _a) {
		m_GlobalAmbient = ((_r & 0xFF) << 24) + ((_g & 0xFF) << 16) + ((_b & 0xFF) << 8) + _a;
	}
	static void enableGlobalLight() {
        if(m_GlobalAmbient != null)
            gx.setLightGlobalAmbient(m_GlobalAmbient, true);
    }
	static void disableGlobalLight() {
        if(m_GlobalAmbient != null)
            gx.setLightGlobalAmbient(m_GlobalAmbient, false);
    }

	public void set(TLightMode _kind, Integer _ambient, Integer _diffuse, Integer _specular, Point3D _position, Vector3D _direction, float _exposant, float _coneangle, float _constant, float _linear, float _quadratic) {
        mode					= _kind;
        ambientColor			= _ambient;
        diffuseColor			= _diffuse;
        specularColor			= _specular;
        m_Position				= _position;
        m_Direction				= _direction;
        m_Exposant				= _exposant;
        m_ConeAngle				= _coneangle;
        m_ConstantAttenuation	= _constant;
        m_LinearAttenuation		= _linear;
        m_QuadraticAttenuation	= _quadratic;
    }

	public void setType(TLightMode _kind) {
        mode = _kind;
    }
	public void setAmbient(Integer _ambient) {
        ambientColor = _ambient;
    }
	public void setDiffuse(Integer _diffuse) {
        diffuseColor = _diffuse;
    }
	public void setSpecular(Integer _specular) {
        specularColor = _specular;
    }
	public void setPosition(Point3D _position) {
        m_Position = _position;
    }
	public void setDirection(Vector3D _direction) {
        m_Direction = _direction;
    }
	public void setExposant(float _exposant) {
        m_Exposant = _exposant;
    }
	public void setConeAngle(float _coneangle) {
        m_ConeAngle = _coneangle;
    }
	public void setConstantAttenuation(float _constant) {
        m_ConstantAttenuation = _constant;
    }
	public void setLinearAttenuation(float _linear) {
        m_LinearAttenuation = _linear;
    }
	public void setQuadraticAttenuation(float _quadratic) {
        m_QuadraticAttenuation = _quadratic;
    }

	public void translate(float _x, float _y, float _z) {
		m_Position = m_Position.plus(_x, _y, _z);
//      m_Position.plusEquals(_x, _y, _z);
    }
	public void translate(Vector3D _translation) {
		m_Position = m_Position.plus(_translation);
//        m_Position.plusEquals(_translation);
    }

	public void rotate(float _x, float _y, float _z) {
		m_Direction = m_Direction.plus(_x, _y, _z);
//		m_Direction.plusEquals(_x, _y, _z);
    }
	public void rotate(Vector3D _rotations) {
		m_Direction = m_Direction.plus(_rotations);
//		m_Direction.plusEquals(_rotations);
    }

	@Override
	public void enable() {
        gx.setLight(lightID, true);
        gx.setLight(lightID, LightParameter.LGT_POSITION, m_Position);

        gx.setLight(lightID, LightParameter.LGT_AMBIENT, ambientColor);
        gx.setLight(lightID, LightParameter.LGT_DIFFUSE, diffuseColor);
        gx.setLight(lightID, LightParameter.LGT_SPECULAR, specularColor);

        gx.setLight(lightID, LightParameter.LGT_SPOT_CUTOFF, 1.0f);
        gx.setLight(lightID, LightParameter.LGT_SPOT_EXPONENT, specularColor);
        gx.setLight(lightID, LightParameter.LGT_SPOT_DIRECTION, m_Direction);

        gx.setLight(lightID, LightParameter.LGT_CONSTANT_ATTENUATION, specularColor);
        gx.setLight(lightID, LightParameter.LGT_LINEAR_ATTENUATION, specularColor);
        gx.setLight(lightID, LightParameter.LGT_QUADRATIC_ATTENUATION, specularColor);
    }
	@Override
	public void disable() {
        gx.setLight(lightID, false);
    }

	@Override
	public void process() {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public ID getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GxParent getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

}
