package fr.threedijnns.api.lang.stream.handlers;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import fr.java.lang.enums.PixelFormat;

public interface StreamHandler2D extends StreamHandler {

	int 		getWidth();
	int 		getHeight();
	PixelFormat getPixelFormat();

	@Deprecated
	default void process(final int _size, final ByteBuffer _data, final Semaphore _signal) {};

	void process(final int _width, final int _height, final ByteBuffer _data, final int _stride, final Semaphore _signal);

}