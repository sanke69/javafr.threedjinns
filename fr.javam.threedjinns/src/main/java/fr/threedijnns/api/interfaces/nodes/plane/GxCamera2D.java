package fr.threedijnns.api.interfaces.nodes.plane;

import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.plane.Vector2D;
import fr.threedijnns.api.interfaces.nodes.GxCamera;

public interface GxCamera2D extends GxCamera<Point2D, Vector2D> {

	public void	hMove	(float _step);
	public void	vMove	(float _step);

	public void	rotate	(float _step);

}
