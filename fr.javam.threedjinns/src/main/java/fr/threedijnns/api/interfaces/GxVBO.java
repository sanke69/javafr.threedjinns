package fr.threedijnns.api.interfaces;

import fr.threedijnns.gx;
import fr.threedijnns.api.lang.declarations.IDeclaration;

public interface GxVBO {

	public IDeclaration getDeclaration();
	
	public default void build() { gx.runLater(() -> buildVBOs()); }
	public void			buildVBOs();

}
