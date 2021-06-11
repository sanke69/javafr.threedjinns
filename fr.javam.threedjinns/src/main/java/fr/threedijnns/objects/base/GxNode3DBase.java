package fr.threedijnns.objects.base;

import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.space.BoundingBox3D;
import fr.java.math.geometry.space.Frame3D;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.Space;
import fr.java.maths.geometry.space.types.SimpleDimension3D;
import fr.java.maths.geometry.types.Points;
import fr.threedijnns.api.interfaces.nodes.space.GxNode3D;

public abstract class GxNode3DBase extends GxNodeBase implements GxNode3D {

	protected Frame3D		frame;
	protected BoundingBox3D.Editable	boundBox;
	protected Vector3D		scale;

	GxNode3DBase() {
		super();

		frame     = Space.newFrame();
		boundBox  = Space.newCube();
		scale     = Vectors.of(1, 1, 1);
	}

	public Frame3D 			getFrame() {
		return frame;
	}
	public BoundingBox3D 	getBoundaries() {
		return boundBox;
	}
	public Vector3D 		getScale() {
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
	}
//	@Override
	public void setPosition(float _tx, float _ty, float _tz) {
		boundBox.setPosition(Points.of(_tx, _ty, _tz));
        frame.setOrigin(Points.of(_tx, _ty, _tz));
	}
//	@Override
//	public void setRotation(float _rx, float _ry, float _rz) {
 //       frame.rotateOrigin(_rx, _ry, _rz);
  //  }

}
