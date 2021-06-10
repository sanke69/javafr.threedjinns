package fr.threedijnns.api.interfaces.nodes.space;

import fr.threedijnns.api.interfaces.GxNode;
import fr.threedijnns.api.interfaces.GxParent;

public interface GxLight extends GxNode {

	public GxParent	getParent();
	public void 	enable();
	public void 	disable();

}
