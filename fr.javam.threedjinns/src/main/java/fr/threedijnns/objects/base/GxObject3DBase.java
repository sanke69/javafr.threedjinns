package fr.threedijnns.objects.base;

import java.util.HashSet;
import java.util.Set;

import fr.java.math.geometry.Frame;
import fr.java.math.geometry.Geometry;
import fr.java.maths.geometry.Space;
import fr.java.math.geometry.space.BoundingBox3D;
import fr.java.math.geometry.space.Frame3D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.math.geometry.space.shapes.Cube3D;
import fr.java.maths.Points;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.space.types.SimpleDimension3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.attributes.Material;
import fr.threedijnns.api.attributes.Texture2D;
import fr.threedijnns.api.interfaces.GxParent;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.interfaces.nodes.space.GxNode3D;
import fr.threedijnns.api.interfaces.nodes.space.GxObject3D;
import fr.threedijnns.api.lang.buffer.texture.ITexture;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.api.lang.enums.PolygonMode;

public abstract class GxObject3DBase extends GxNode3DBase implements GxObject3D, GxParent {

	protected Frame3D		 			frame;
	protected BoundingBox3D.Editable	boundBox;
	protected Vector3D		 			scale;

	protected Set<GxNode3D>				children;

	protected PolygonMode	 			polygonMode;
	protected FaceType		 			faceType;

	protected boolean					isColorEnabled;
	protected Integer					color;

	protected boolean					isMaterialEnabled;
	protected Material					material;

	protected boolean					isTextureEnabled;
	protected ITexture					texture;

	protected GxObject3DBase() {
		super();

		frame          		= Space.newFrame();
		boundBox        	= Space.newCubeUnit();
		scale           	= Vectors.of(1, 1, 1);

		children			= new HashSet<GxNode3D>();
		
		color           	= null;
		isColorEnabled    	= false;

		material        	= null;
		isMaterialEnabled 	= false;

		texture         	= null;
		isTextureEnabled  	= false;

		polygonMode     	= PolygonMode.Realistic;
		faceType        	= FaceType.FrontAndBack;
	}
	protected GxObject3DBase(double _w, double _h, double _d) {
		this();
		boundBox = Space.newCube(-_w/2, -_h/2, -_d/2, _w, _h, _d);
		frame    = Space.newFrame();
	}

	@Override
	public Set<GxNode3D> getChildren() {
		return children;
	}

	public PolygonMode getPolygonMode() {
		return polygonMode;
	}
	public FaceType    getFaceType() {
		return faceType;
	}
	
	public boolean   isColorEnabled() {
        return isColorEnabled;
    }
	public boolean   hasColor() {
        return color != null;
    }
	public Integer   getColor() {
		return color;
	}

	public boolean   isMaterialEnabled() {
        return isMaterialEnabled;
    }
	public boolean   hasMaterial() {
        return material != null;
    }
	public Material  getMaterial() {
		return material;
	}

	public boolean   isTextureEnabled() {
        return isTextureEnabled;
    }
	public boolean   hasTexture() {
        return texture != null;
    }
	public ITexture getTexture() {
		if(texture == null)
			texture = new Texture2D();
		return texture;
	}

	public Frame3D getFrame() {
		return frame;
	}
	public BoundingBox3D getBoundaries() {
		return boundBox;
	}
	public Vector3D getScale() {
		return scale;
	}

	public void setBoundingBox(BoundingBox3D _newBoundingBox) {
		boundBox.set(_newBoundingBox);
	}

	
	
//	@Override
	public void setSize(float _w, float _h, float _d) {
		boundBox.setSize(SimpleDimension3D.of(_w, _h, _d));
    }
//	@Override
	public void setScale(float _tw, float _th, float _td) {
        scale = Vectors.of(_tw, _th, _td);
//        scale.set(_tw, _th, _td);
	}
//	@Override
	public void setPosition(float _tx, float _ty, float _tz) {
		boundBox.setPosition(Points.of(_tx, _ty, _tz));
        frame.setOrigin(Points.of(_tx, _ty, _tz));
	}
//	@Override
//	public void setRotation(float _rx, float _ry, float _rz) {
 //       locate.rotateOrigin(_rx, _ry, _rz);
  //  }


//	@Override
	public void setPolygonMode(PolygonMode _pm) {
        polygonMode = _pm;
    }
//	@Override
	public void setFaceType(FaceType _ft) {
        faceType = _ft;
    }


//	@Override
	public void enableColor(boolean _enable) {
        isColorEnabled = _enable;
    }
//	@Override
	public void setColor(byte _red, byte _green, byte _blue, byte _alpha) {
        color = _red << 0xFF000000 + _green << 0x00FF0000 + _blue << 0x0000FF00 + _alpha;
    }
	public void setColor(int _red, int _green, int _blue, int _alpha) {
        color = _red << 0xFF000000 + _green << 0x00FF0000 + _blue << 0x0000FF00 + _alpha;
    }
	public void setColor(int _color) {
        color = _color;
    }

//	@Override
	public void enableMaterial(boolean _enable) {
        isMaterialEnabled = _enable;
    }
//	@Override
	public void setMaterial(Material _material) {
        material = _material;
    }

//	@Override
	public void enableTexture(boolean _enable) {
        isTextureEnabled = _enable;
    }
//	@Override
	public void setTexture(ITexture _texture) {
        texture = _texture;
    }

	@Override
	public void process() {
		for(GxNode3D child : children)
			child.process();
	}

	public void preRender() {
		gx.pushMatrix(MatrixType.MAT_MODELVIEW);
		gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, frame.getModelMatrix());
//		gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, scale);

		gx.setPolygonMode(polygonMode, faceType);
		
		if(isColorEnabled())
			gx.setColor(color);
		if(isMaterialEnabled())
			material.enable();
		if(isTextureEnabled() && texture != null)
			texture.enable(0);
	}
	public void postRender() {
		if(isTextureEnabled() && texture != null)
			texture.disable(0);
		if(isMaterialEnabled())
			material.disable();
		if(isColorEnabled())
			gx.setColor(0xFFFFFFFF);
		
		if(getChildren().size() > 0)
			for(GxNode3D child : getChildren())
				if(child instanceof GxRenderable)
					((GxRenderable) child).render();

		gx.popMatrix(MatrixType.MAT_MODELVIEW);
	}

	public int compareTo(Frame<Point3D, Vector3D> o) {
		return frame.compareTo(o);
	}

}
