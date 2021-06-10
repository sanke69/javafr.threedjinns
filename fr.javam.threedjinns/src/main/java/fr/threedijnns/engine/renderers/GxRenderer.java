package fr.threedijnns.engine.renderers;

import java.awt.image.BufferedImage;

import fr.threedijnns.objects.plane.Scene2D;
import fr.threedijnns.objects.space.Scene3D;

public interface GxRenderer {

//	public BufferedImage	getRendering();

	public void 			setScene2D(Scene2D _scene);
	public void 			setScene3D(Scene3D _scene);

	public void process();
	public void render();

}
