package fr.threedijnns.objects.space.camera;

import fr.java.lang.exceptions.NotYetImplementedException;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.algebra.matrices.DoubleMatrix44;
import fr.java.maths.geometry.space.camera.Projections3D;
import fr.java.maths.geometry.space.camera.behaviors.QuaternionCameraBehaviorBase;
import fr.java.maths.geometry.space.types.SimpleRay3D;
import fr.threedijnns.api.interfaces.nodes.space.GxCamera3D;

public class GxDefaultCamera3D extends QuaternionCameraBehaviorBase implements GxCamera3D {
	Projections3D 	projection;

	public GxDefaultCamera3D() {
		super();
		projection = Projections3D.perspective(69.f, 16.f / 9.f, 1.f, 1e9f);
	}
	public GxDefaultCamera3D(Projections3D _projection) {
		super();
		projection = _projection;
	}

	@Override
	public Projections3D getProjection() {
		return projection;
	};

	@Override
	public DoubleMatrix44 	projectionMatrix() {
		return getProjection().asUniformMatrix();
	}
	@Override
	public DoubleMatrix44 	modelviewMatrix() {
//		return getFrame().getModelMatrix().inverse();
		return getViewMatrix();
	}

	@Override
	public Point2D inImage(Point3D _pt) {
		throw new NotYetImplementedException();
	}
//	@Override
	public SimpleRay3D inSpace(Point2D _pxl) {
		throw new NotYetImplementedException();
	}

}
