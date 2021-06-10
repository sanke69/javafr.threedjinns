package fr.threedijnns.objects.base;

import fr.java.jvm.properties.id.IDs;
import fr.java.lang.properties.ID;
import fr.threedijnns.api.interfaces.GxNode;
import fr.threedijnns.api.interfaces.GxParent;

public abstract class GxNodeBase implements GxNode {

	protected final ID	id;
	protected GxParent	parent;
	protected boolean 	isVisible;

	GxNodeBase() {
		super();
		id        = IDs.random(256);
		parent    = null;
		isVisible = false;
	}

	@Override
	public ID 		getId() { return id; }

	@Override
	public GxParent getParent() { return parent; }
	public void 	setParent(GxParent _parent) { parent = _parent; }

	@Override
	public boolean 	isVisible() { return isVisible; }
	public void 	setVisible(boolean _visible) { isVisible = _visible; }

	@Override
	public void 	show() { isVisible = true; }
	@Override
	public void 	hide() { isVisible = false; }

}
