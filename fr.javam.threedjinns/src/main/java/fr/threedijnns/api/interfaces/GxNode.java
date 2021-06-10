package fr.threedijnns.api.interfaces;

import fr.java.lang.properties.ID;
import fr.java.patterns.composite.Component;

public interface GxNode extends Component {

	public ID		getId();
	public GxParent	getParent();

	public boolean 	isVisible();

	public void 	show();
	public void 	hide();

	public void 	process();

}
