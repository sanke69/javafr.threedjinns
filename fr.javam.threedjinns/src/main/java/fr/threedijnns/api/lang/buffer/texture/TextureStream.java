package fr.threedijnns.api.lang.buffer.texture;

import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public interface TextureStream {

	StreamHandler2D getHandler();

	int 			getWidth();
	int 			getHeight();

	void 			snapshot();
	void 			tick();
	void 			destroy();

}