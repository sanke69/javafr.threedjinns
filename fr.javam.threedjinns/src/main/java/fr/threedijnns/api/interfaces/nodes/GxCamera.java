package fr.threedijnns.api.interfaces.nodes;

import java.util.Optional;

import fr.java.math.algebra.NumberVector;
import fr.java.math.geometry.Frame;
import fr.java.math.geometry.Point;
import fr.java.maths.algebra.matrices.Matrix44d;
import fr.java.maths.geometry.space.camera.CameraModel;

public interface GxCamera<P extends Point, V extends NumberVector> extends CameraModel {

	public Frame<P,V>					getFrame();

	public Optional<P>					getTarget			();
	public void							lock				(P _target);
	public void							unlock				();

	public Matrix44d 					projectionMatrix	();
	public Matrix44d 					modelviewMatrix		();

}
