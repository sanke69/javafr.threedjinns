package fr.threedijnns.api.interfaces.nodes.space;

import fr.java.math.geometry.space.Frame3D;
import fr.java.math.geometry.space.Point3D;
import fr.threedijnns.api.interfaces.GxNode;
import fr.threedijnns.api.interfaces.GxParent;

public interface GxNode3D extends GxNode {

	public GxParent			getParent();
	public Frame3D 			getFrame();
	public default Point3D 	getPosition() { return getFrame().getOrigin(); }

}
