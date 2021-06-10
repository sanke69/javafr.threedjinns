package fr.threedijnns.api.graphics;

import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.engine.renderers.GxRenderer;

public interface GxViewport {

	GxRenderer   	getRenderer();
	IFrameStream 	getRenderStream();
	StreamHandler2D getRenderingStream();

}
