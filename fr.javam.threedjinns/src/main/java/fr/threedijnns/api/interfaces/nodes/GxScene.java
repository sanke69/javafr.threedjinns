package fr.threedijnns.api.interfaces.nodes;

import fr.threedijnns.api.interfaces.GxParent;

public interface GxScene extends GxParent, GxRenderable {

	void			setCamera      (GxCamera<?,?> _camera);
	GxCamera<?,?>	getCamera      ();

}
