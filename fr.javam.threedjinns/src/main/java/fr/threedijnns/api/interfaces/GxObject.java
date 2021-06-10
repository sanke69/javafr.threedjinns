package fr.threedijnns.api.interfaces;

import fr.threedijnns.api.attributes.Material;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.lang.buffer.texture.ITexture;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.PolygonMode;

public interface GxObject extends GxParent, GxRenderable {

	PolygonMode 		getPolygonMode();	// TODO:: Change to RenderMode !!!
	FaceType 			getFaceType();		// TODO:: Change to RenderFace !!!

	boolean 			isColorEnabled();
	boolean 			hasColor();
	Integer				getColor();

	boolean 			isMaterialEnabled();
	boolean 			hasMaterial();
	Material 			getMaterial();

	boolean 			isTextureEnabled();
	boolean 			hasTexture();
	ITexture 			getTexture();

}
