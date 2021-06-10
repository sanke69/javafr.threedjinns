package fr.threedijnns.api.interfaces.nodes.space;

import fr.java.math.geometry.space.BoundingBox3D;
import fr.java.math.geometry.space.Vector3D;

import fr.threedijnns.api.interfaces.GxObject;

public interface GxObject3D extends GxObject, GxNode3D {

	public BoundingBox3D 	getBoundaries();
	public Vector3D 		getScale();

}
