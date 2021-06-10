package fr.threedijnns.objects.base;

import java.util.HashSet;
import java.util.Set;

import fr.threedijnns.api.interfaces.GxNode;
import fr.threedijnns.api.interfaces.GxParent;

public class GxGroupBase extends GxNodeBase implements GxParent {

	protected Set<GxNode> children;

	public GxGroupBase() {
		super();
		children  = new HashSet<GxNode>();
	}

	@Override
	public Set<GxNode> getChildren() {
		return children;
	}

	@Override
	public void process() {
		for(GxNode node : children)
			node.process();
	}

}
