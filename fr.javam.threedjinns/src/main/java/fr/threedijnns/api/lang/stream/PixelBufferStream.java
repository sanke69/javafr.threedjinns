package fr.threedijnns.api.lang.stream;

import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public interface PixelBufferStream {

	void 			bind(StreamHandler2D _handler);

	StreamHandler2D getHandler();

	int 			getWidth();
	int 			getHeight();

	void 			snapshot();
	void 			tick();
	void 			destroy();

}