package fr.javafx.rendering;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import fr.javafx.scene.image.DirectWritableImage;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.PixelFormat;

public class DoubleBufferFrameHandler implements StreamHandler2D {
	static final int period_ms = 20;

	protected DirectWritableImage 					renderImageNext;
	protected long 									lastUpdateTimeMillis;

	protected ObjectProperty<DirectWritableImage> 	imageProperty;
	protected int 									w, h;

	public DoubleBufferFrameHandler(ObjectProperty<DirectWritableImage> _imageProperty) {
		super();
		
		imageProperty = _imageProperty;
		w = (int) imageProperty.get().getWidth();
		h = (int) imageProperty.get().getHeight();
	}

	public int getWidth() {
		return (int) w;
	}
	public int getHeight() {
		return (int) h;
	}
	public fr.java.lang.enums.PixelFormat getPixelFormat() {
		return fr.java.lang.enums.PixelFormat.PXF_BGRA8;
	}

	public void process(final int width, final int height, final ByteBuffer data, final int stride, final Semaphore signal) {
		assert (data != null);
		assert (signal != null);

		data.rewind();
		assert (data.remaining() == stride * height);

		long currentUpdateTimeMillis = System.currentTimeMillis();
		if (currentUpdateTimeMillis - lastUpdateTimeMillis < period_ms) {
			signal.release();
			return;
		}
		lastUpdateTimeMillis = currentUpdateTimeMillis;

		ensureRenderImages(width, height);
		renderImageNext.setPixelsNoNotify(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), data, stride);
		swapRenderImages();

		// Notify the render thread that we're done processing
		signal.release();
	}

	protected void ensureRenderImages(int width, int height) {
		if (imageProperty.get() == null || (int) imageProperty.get().getWidth() != width || (int) imageProperty.get().getHeight() != height) {
			imageProperty.set(new DirectWritableImage(width, height));
		}
		if (renderImageNext == null || (int) renderImageNext.getWidth() != width
				|| (int) renderImageNext.getHeight() != height) {
			renderImageNext = new DirectWritableImage(width, height);
		}
	}

	protected void swapRenderImages() {
		DirectWritableImage renderImageTemp = imageProperty.get();
		imageProperty.set(renderImageNext);
		renderImageNext = renderImageTemp;
	}

}
