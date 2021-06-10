package fr.threedijnns.api.attributes;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import fr.java.lang.enums.PixelFormat;
import fr.java.math.algebra.tensor.ByteTensor;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.java.maths.geometry.plane.types.SimpleDimension2D;
import fr.media.image.utils.BufferedImages;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.texture.ITexture;
import fr.threedijnns.api.lang.buffer.texture.TextureBase;
import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public class Texture2D implements ITexture {

	private TextureBase  	textureBase;
	private TextureStream	textureStream;

	public Texture2D() {
		super();
		textureBase   = null;
		textureStream = null;
	}
	public Texture2D(int _w, int _h, PixelFormat _pxl_fmt, boolean _mipmap) {
		super();

        if(_w != 0 && _h != 0 && _pxl_fmt != null)
            createTextureBase(_w, _h, _pxl_fmt);
	}
	
	public TextureBase getBase() {
		return textureBase;
	}

	@Override
    public void enable(int _unit) {
		if(textureBase != null)
			textureBase.enable(_unit);
    }
	@Override
	public void disable(int _unit) {
		if(textureBase != null)
			textureBase.disable(_unit);
	}

	public void update(final ByteBuffer _buffer, final int _w, final int _h, final int _stride, final SimpleRectangle2D _in, final SimpleRectangle2D _out) {
		if(textureBase == null)
            createTextureBase(_w, _h, PixelFormat.PXF_ABGR8);

		updateTextureBase(_w, _h, _stride, _buffer);
	}

	public void update(final BufferedImage _img, final SimpleRectangle2D _in, final SimpleRectangle2D _out) {
		if(textureBase != null && (textureBase.getWidth() != _img.getWidth() || textureBase.getHeight() != _img.getHeight())) {
			gx.runLater(() -> gx.deleteTexture2D(textureBase));
			textureBase = null;
		}

		if(textureBase == null)
            createTextureBase(_img.getWidth(), _img.getHeight(), PixelFormat.PXF_RGBA8);

		if(textureBase != null)
			updateTextureBase(_img.getWidth(), _img.getHeight(), _img.getWidth() * textureBase.getPixelFormat().bytesPerPixel(), BufferedImages.getPixelBuffer(_img, textureBase.getPixelFormat()));
	}
	public void update(final ByteTensor _img, final SimpleRectangle2D _in, final SimpleRectangle2D _out) {
		int width  = _img.getSliceDimension(0, true);
		int height = _img.getSliceDimension(1, true);
		PixelFormat imgPxf = PixelFormat.of(_img.getSliceDimension(2, true));
		if(textureBase == null)
            createTextureBase(width, height, imgPxf);

		updateTextureBase(width, height, width * imgPxf.bytesPerPixel(), _img.getBuffer());
	}

	public void bind(final StreamHandler2D _handler) {
		if(textureBase == null)
            createTextureBase(_handler.getWidth(), _handler.getHeight(), _handler.getPixelFormat());

		gx.runLater(() -> textureStream = gx.createTextureStream2D(textureBase, _handler, 3));
	}
	public void unbind() {
		textureStream = null;		
	}

	public void snapshot() {
		if(textureStream == null)
			return ;

		gx.runLater(() -> textureStream.snapshot());
	}
	public void tick() {
		if(textureStream == null)
			return ;

		gx.runLater(() -> textureStream.tick());
	}

	private void createTextureBase(final int _w, final int _h, final PixelFormat _pxl_fmt) {
    	if(textureBase != null)
    		return ;

    	gx.runLater( () -> textureBase = gx.createTexture2D(SimpleDimension2D.of(_w, _h), _pxl_fmt, null) );
    }
    private void updateTextureBase(final int _w, final int _h, final int _stride, final Buffer _buffer) {
    	if(textureBase == null)
			return ;

    	gx.runLater( () -> textureBase.update(0, _h * _stride, LockFlag.WriteOnly, _buffer) );
    }

}

