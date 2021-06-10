package fr.threedijnns.objects.base;

import java.util.Set;

import fr.java.math.geometry.BoundingBox;
import fr.java.math.geometry.plane.Frame2D;
import fr.java.math.geometry.plane.Vector2D;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.Plane;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.threedijnns.api.attributes.Material;
import fr.threedijnns.api.attributes.Texture2D;
import fr.threedijnns.api.interfaces.GxParent;
import fr.threedijnns.api.interfaces.nodes.plane.GxNode2D;
import fr.threedijnns.api.interfaces.nodes.plane.GxObject2D;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.PolygonMode;

public abstract class GxObject2DBase extends GxNodeBase implements GxParent, GxObject2D {

	protected Frame2D		 	m_Locate;
	protected BoundingBox.TwoDims.Editable		m_BoundBox;
	protected Vector2D 			m_Scale;

	protected Set<GxNode2D>		children;

	protected PolygonMode	 	m_PolygonMode;
	protected FaceType		 	m_FaceType;

	protected boolean			m_ColorEnabled;
	protected Integer			m_Color;

	protected boolean			m_MaterialEnabled;
	protected Material			m_Material;

	protected boolean			m_TextureEnabled;
	protected Texture2D			m_Texture;

	protected GxObject2DBase() {
		super();

		m_Locate          = Plane.newFrame();
		m_BoundBox        = new SimpleRectangle2D();
		m_Scale           = Vectors.of(1, 1);

		m_PolygonMode     = PolygonMode.Realistic;
		m_FaceType        = FaceType.FrontAndBack;

		m_ColorEnabled    = false;
		m_Color           = null;

		m_MaterialEnabled = false;
		m_Material        = null;

		m_TextureEnabled  = false;
		m_Texture         = null;

	}
	protected GxObject2DBase(float _w, float _h, float _d) {
		this();
		m_BoundBox        = new SimpleRectangle2D(0.0f, 0.0f, _w, _h);
	}

	@Override
	public Set<GxNode2D> getChildren() {
		return children;
	}


	public PolygonMode getPolygonMode() {
		return m_PolygonMode;
	}
	public FaceType    getFaceType() {
		return m_FaceType;
	}
	
	public boolean   isColorEnabled() {
        return m_ColorEnabled;
    }
	public boolean   hasColor() {
        return m_Color != null;
    }
	public Integer   getColor() {
		return m_Color;
	}

	public boolean   isMaterialEnabled() {
        return m_MaterialEnabled;
    }
	public boolean   hasMaterial() {
        return m_Material != null;
    }
	public Material  getMaterial() {
		return m_Material;
	}

	public boolean   isTextureEnabled() {
        return m_TextureEnabled;
    }
	public boolean   hasTexture() {
        return m_Texture != null;
    }
	public Texture2D getTexture() {
		if(m_Texture == null)
			m_Texture = new Texture2D();
		return m_Texture;
	}

	public Frame2D getLocate() {
		return m_Locate;
	}
	public BoundingBox.TwoDims getBoundaries() {
		return m_BoundBox;
	}
	public Vector2D getScale() {
		return m_Scale;
	}


	public void setPolygonMode(PolygonMode _pm) {
        m_PolygonMode = _pm;
    }
	public void setFaceType(FaceType _ft) {
        m_FaceType = _ft;
    }

	public void color(boolean _enable) {
        m_ColorEnabled = _enable;
    }
	public void setColor(Integer _color) {
        m_Color = _color;
    }

	public void material(boolean _enable) {
        m_MaterialEnabled = _enable;
    }
	public void setMaterial(Material _material) {
        m_Material = _material;
    }

	public void texture(boolean _enable) {
        m_TextureEnabled = _enable;
    }
	public void setTexture(Texture2D _texture) {
        m_Texture = _texture;
    }


	public void setSize(float _w, float _h) {
		// m_BoundBox.setSize(_w, _h);
    }
	public void setScale(float _tw, float _th) {
        m_Scale = Vectors.of(_tw, _th);
//        m_Scale.set(_tw, _th);
	}
	public void setPosition(float _tx, float _ty) {
		// m_BoundBox.setPosition(_tx, _ty);
//        m_Locate.translate(_tx, _ty);
	}
	public void setRotation(float _rz) {
//        m_Locate.rotateOrigin(_rz);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public void setSize(float _w, float _h) {
		m_BoundBox.setSize(_w, _h);
    }
	public void setScale(float _tw, float _th) {
        m_Scale.set(_tw, _th, 1.0f);
	}
	public void setPosition(float _tx, float _ty) {
		m_BoundBox.setPosition(_tx, _ty);
//        m_Locate.translate(_tx, _ty);
	}
	public void setRotation(float _rz) {
//        m_Locate.rotateOrigin(_rz);
    }

	public void color(boolean _enable) {
        m_ColorEnabled = _enable;
    }
	public void setColor(Integer _color) {
        m_Color = _color;
    }

	public void material(boolean _enable) {
        m_MaterialEnabled = _enable;
    }
	public void setMaterial(Material _material) {
        m_Material = _material;
    }

	public void texture(boolean _enable) {
        m_TextureEnabled = _enable;
    }
	public void setTexture(Texture2D _texture) {
        m_Texture = _texture;
    }

	public void setPolygonMode(PolygonMode _pm) {
        m_PolygonMode = _pm;
    }
	public void setFaceType(FaceType _ft) {
        m_FaceType = _ft;
    }

	public void preRender() {
		gx.pushMatrix(MatrixType.MAT_MODELVIEW);
		gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, getLocate());
		gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, m_Scale);
		
		gx.setPolygonMode(m_PolygonMode, m_FaceType);

		if(isColorEnabled())
			gx.setColor(m_Color);
		if(isMaterialEnabled())
			m_Material.enable();
		if(isTextureEnabled() && m_Texture != null)
			m_Texture.enable(0);
	}
	public void postRender() {
		if(isTextureEnabled() && m_Texture != null)
			m_Texture.disable(0);
		if(isMaterialEnabled())
			m_Material.disable();
		if(isColorEnabled())
			gx.setColor(0xFFFFFFFF);

		gx.popMatrix(MatrixType.MAT_MODELVIEW);
	}
*/
}
