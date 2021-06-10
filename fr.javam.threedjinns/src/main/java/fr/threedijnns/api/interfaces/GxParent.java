package fr.threedijnns.api.interfaces;

import java.util.Set;

import fr.java.patterns.composite.Composite;

public interface GxParent extends GxNode, Composite {

//	public GxNode		getChild(int _i);
	public Set<? extends GxNode> getChildren();

}
