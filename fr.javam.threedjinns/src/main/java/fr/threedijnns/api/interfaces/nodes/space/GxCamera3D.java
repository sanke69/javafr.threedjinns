package fr.threedijnns.api.interfaces.nodes.space;

import java.util.Optional;

import fr.java.lang.exceptions.IllegalAccessArgument;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.algebra.matrices.Matrix44d;
import fr.java.maths.geometry.space.camera.CameraBehavior;
import fr.java.maths.geometry.space.camera.Projections3D;
import fr.threedijnns.api.interfaces.nodes.GxCamera;

public interface GxCamera3D extends GxCamera<Point3D, Vector3D>, CameraBehavior {

	public Optional<Point3D>		getTarget	();
	public void						lock		(Point3D _target);
	public void						unlock		();

	public default Projections3D	getProjection() { throw new IllegalAccessArgument(); }

	public Matrix44d 				projectionMatrix();
	public Matrix44d 				modelviewMatrix();

}
