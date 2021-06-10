package fr.javafx.scene.image;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

public class DirectWritableImage extends WritableImage {

	protected static Method	getWritablePlatformImageMethod;
/*
	static {
		try {
			XRaster.Collection.registerNewSupplier(DirectWritableImage.class, (Function<WritableImage, XRaster>) 	XRasterImageFx::new);
			
			getWritablePlatformImageMethod = Image.class.getDeclaredMethod("getWritablePlatformImage");
			getWritablePlatformImageMethod.setAccessible(true);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
*/
	public DirectWritableImage(int width, int height) {
		super(width, height);
	}

	public DirectWritableImage(PixelReader reader, int width, int height) {
		super(reader, width, height);
	}

	public DirectWritableImage(PixelReader reader, int x, int y, int width, int height) {
		super(reader, x, y, width, height);
	}

	public <T extends Buffer> void setPixelsNoNotify(int x, int y, int w, int h, PixelFormat<T> pixelformat, T buffer, int scanlineStride) {
		//WritableImage img = new WritableImage(w, h);
		getPixelWriter().setPixels(x, y, w, h, pixelformat, buffer, scanlineStride);
//		PlatformImage pimg = getWritablePlatformImageAlt();
//		pimg.setPixels(x, y, w, h, pixelformat,	buffer, scanlineStride);
	}
	public void setPixelsNoNotify(int x, int y, int w, int h, PixelFormat<ByteBuffer> pixelformat, byte buffer[], int offset, int scanlineStride) {
		//WritableImage img = new WritableImage(w, h);
		WritablePixelFormat<ByteBuffer> format = (WritablePixelFormat<ByteBuffer>) WritablePixelFormat.getByteRgbInstance();
		getPixelWriter().setPixels(x, y, w, h, format, buffer, 0, scanlineStride);
//		PlatformImage pimg = getWritablePlatformImageAlt();
//		pimg.setPixels(x, y, w, h, pixelformat,	buffer, offset, scanlineStride);
	}
	public void setPixelsNoNotify(int x, int y, int w, int h, PixelFormat<IntBuffer> pixelformat, int buffer[], int offset, int scanlineStride) {
		//WritableImage img = new WritableImage(w, h);
		WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
		getPixelWriter().setPixels(x, y, w, h, format, buffer, 0, scanlineStride);
//		PlatformImage pimg = getWritablePlatformImageAlt();
//		pimg.setPixels(x, y, w, h, pixelformat,	buffer, offset, scanlineStride);
	}
	public void setPixelsNoNotify(int writex, int writey, int w, int h,	PixelReader reader, int readx, int ready) {
		//WritableImage img = new WritableImage(w, h);
		getPixelWriter().setPixels(writex, writey, w, h, reader, readx, ready);

//		PlatformImage pimg = getWritablePlatformImageAlt();
//		pimg.setPixels(writex, writey, w, h, reader, readx, ready);
	}
/*
	protected PlatformImage getWritablePlatformImageAlt() {
		try {
			return (PlatformImage) getWritablePlatformImageMethod.invoke(this);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
*/
}
