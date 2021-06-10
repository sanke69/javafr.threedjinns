package fr.threedijnns.objects.plane.camera;

import java.util.Optional;

import fr.java.math.geometry.plane.Frame2D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.algebra.matrices.Matrix44d;
import fr.java.maths.geometry.Plane;
import fr.java.maths.geometry.space.camera.Projections3D;
import fr.java.maths.geometry.space.types.SimpleRay3D;
import fr.threedijnns.api.interfaces.nodes.plane.GxCamera2D;

public class DefaultCamera2D implements GxCamera2D {
	private Frame2D			frame;
	private Point2D			target;

	private Projections3D 	projection;
	private Matrix44d 		modelview;

	public DefaultCamera2D(double _left, double _right, double _top, double _bottom) {
		super();
		projection 	= Projections3D.ortho(_left, _right, _bottom, _top, -1, 1);
		modelview   = Matrix44d.identity();

		frame		= Plane.newFrame();
		target		= null;
	}

	public Optional<Point2D> getTarget() {
		return target != null ? Optional.of(target) : Optional.empty();
	}
	public void	lock(Point2D _target) {
		target = _target;
	}
	public void	unlock() {
		target = null;
	}

	public Frame2D getFrame() {
		return frame;
	}

	public Matrix44d projectionMatrix() {
		return projection.asUniformMatrix();
	};
	public Matrix44d modelviewMatrix() {
		if(getFrame() != null && getFrame().getParentFrame() != null) {
			
		}

//		if(getTarget().isPresent() && land().getWorld().isPresent())
//			lookAt(Locate3D.local2world(getTarget().get(), land().getWorld().get()), land().getYAxis());
		return modelview; //Transformation3D.fromLocate(getLocate()).asUniformMatrix();
	}

	@Override
	public void hMove(float _step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vMove(float _step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float _step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point2D inImage(Point3D _pt) {
		throw new IllegalAccessError(); 
	}

//	@Override
	public SimpleRay3D inSpace(Point2D _pxl) {
		throw new IllegalAccessError();
	};

}
