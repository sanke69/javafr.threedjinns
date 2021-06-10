package fr.threedijnns.api.interfaces.nodes.plane;

import fr.java.math.geometry.BoundingBox;
import fr.java.math.geometry.plane.Frame2D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.plane.Vector2D;
import fr.threedijnns.api.interfaces.GxObject;

public interface GxObject2D extends GxObject, GxNode2D {

	public default Point2D 		getPosition() { return getLocate().getOrigin(); }
	public Frame2D 				getLocate();
	public BoundingBox.TwoDims 	getBoundaries();
	public Vector2D 			getScale();

}
