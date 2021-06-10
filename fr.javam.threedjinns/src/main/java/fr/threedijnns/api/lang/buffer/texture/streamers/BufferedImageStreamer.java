package fr.threedijnns.api.lang.buffer.texture.streamers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import fr.java.lang.enums.PixelFormat;
import fr.threedijnns.api.attributes.Texture2D;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public class BufferedImageStreamer extends Thread implements StreamHandler2D {

	private final AtomicLong snapshotRequest;
	private       long       snapshotCurrent;

	final Texture2D     texture;
	final BufferedImage	streamSource;

	public BufferedImageStreamer(final Texture2D _dst, final BufferedImage _src) {
		super();
		snapshotRequest = new AtomicLong();
		snapshotCurrent = -1L;

		texture      = _dst;
		streamSource = _src;
		_dst.bind(this);
	}
	
	@Override
	public int 			getWidth() {
		return streamSource.getWidth();
	}
	@Override
	public int 			getHeight() {
		return streamSource.getHeight();
	}
	@Override
	public PixelFormat 	getPixelFormat() {
		return PixelFormat.PXF_BGRA8;
	}

	@Override
	public void 		process(int _width, int _height, ByteBuffer _buffer, int _stride, Semaphore _signal) {
		int nbChannels = _stride / _width;

		int index = 0;
		for(int j = 0; j < _height; ++j) {
			for(int i = 0; i < _width; ++i) {
				if(streamSource != null) {
					byte[] pixels = ((DataBufferByte) streamSource.getRaster().getDataBuffer()).getData();
					_buffer.put(nbChannels * (j * _width + i) + 2, (byte) pixels[index++]);
					_buffer.put(nbChannels * (j * _width + i) + 1, (byte) pixels[index++]);
					_buffer.put(nbChannels * (j * _width + i),     (byte) pixels[index++]);
					_buffer.put(nbChannels * (j * _width + i) + 3, (byte) 255);
				} else {
					_buffer.put(nbChannels * (j * _width + i),     (byte) 0);
					_buffer.put(nbChannels * (j * _width + i) + 1, (byte) (255 * Math.random()));
					_buffer.put(nbChannels * (j * _width + i) + 2, (byte) 0);
					_buffer.put(nbChannels * (j * _width + i) + 3, (byte) 255);
				}
			}
		}

		_signal.release();
	}

	public void 		run() {
		boolean isRunning = true;
		while(isRunning) {
			snapshotRequest.incrementAndGet();

			final long snapshotRequestID = snapshotRequest.get();
			if(snapshotCurrent < snapshotRequestID && texture != null) {
				texture.snapshot();
				snapshotCurrent = snapshotRequestID;
			}
			if(texture != null)
				texture.tick();

			try { Thread.sleep(50);
			} catch (InterruptedException e) { isRunning = false; }
		}
	}

}
