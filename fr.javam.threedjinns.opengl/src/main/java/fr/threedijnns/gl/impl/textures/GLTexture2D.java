package fr.threedijnns.gl.impl.textures;

import static fr.threedijnns.gl.JOGL.gl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.enums.PixelFormat;
import fr.java.math.geometry.plane.Dimension2D;
import fr.java.maths.geometry.plane.types.SimpleDimension2D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.texture.TextureBase;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.Capability;
import fr.threedijnns.api.lang.enums.FrameType;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.enums.TextureFlag;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.api.utils.MipMapUtils;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLEnums;

public class GLTexture2D extends GLBuffer implements TextureBase {
    protected long			width, height;
    protected PixelFormat	pxlFormat;

    protected Integer		keyColor;

    protected boolean       hasMipmaps;
    protected boolean       autoMipmaps;
    protected int         	nbMipmapsLvl;

    protected FrameType 	activeFrame;

	public GLTexture2D() {
		super(BufferNature.Texture);

		width			= 0;
		height			= 0;
		pxlFormat 		= null;
		
		keyColor 		= null;
		
		hasMipmaps 		= false;
		autoMipmaps 	= false;
		nbMipmapsLvl 	= 0;

		activeFrame 	= FrameType.NoId;
	}

	public GLTexture2D(int _w, int _h, PixelFormat _pxl_fmt) {
		this(_w, _h, _pxl_fmt, null, (TextureFlag[]) null);
	}
	public GLTexture2D(int _w, int _h, PixelFormat _pxl_fmt, TextureFlag... _flags) {
		this(_w, _h, _pxl_fmt, null, _flags);
	}
	public GLTexture2D(int _w, int _h, PixelFormat _pxl_fmt, ByteBuffer _buffer, TextureFlag... _flags) {
		super(BufferNature.Texture, Primitives.BYTE, _w * _h * _pxl_fmt.bytesPerPixel());

		width			= _w;
		height			= _h;
		pxlFormat 		= _pxl_fmt;
		
		keyColor 		= null;
		
		hasMipmaps 		= false;
		nbMipmapsLvl 	= 0;

		activeFrame 	= FrameType.NoId;

	    if(_flags != null && !Arrays.asList(_flags).contains(TextureFlag.TEX_NOMIPMAP))
	    	autoMipmaps = true;

	    autoConfigure(SimpleDimension2D.of((int) width, (int) height), _pxl_fmt, _buffer);
	}

    @Override
    public void enable(int _unit) {
        gl.glEnable(GL.GL_TEXTURE_2D);

	    gl.glActiveTexture(GL.GL_TEXTURE0 + _unit);
        gl.glBindTexture(GL.GL_TEXTURE_2D, glId);
    }
    @Override
    public void disable(int _unit) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    	gl.glActiveTexture(GL.GL_TEXTURE0 + _unit);

    	gl.glDisable(GL.GL_TEXTURE_2D);
    }

    @Override
    public ByteBuffer lock(long _offset, long _size, LockFlag _flags) {
        if(buffer != null)
            return buffer;

        buffer = ByteBuffer.allocate((int) (width * height * pxlFormat.bytesPerPixel()));

        gl.glBindTexture(GL.GL_TEXTURE_2D, glId);
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) width, (int) height, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), buffer);

        return buffer;
	}
    @Override
    public void       unlock() {
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        buffer = null;
	}
    @Override
    public void       update(long _offset, long _size, LockFlag _flags, Buffer _data) {
	    if(_data != null && _size >= width * height) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, glId);

            if(autoMipmaps || (hasMipmaps && nbMipmapsLvl > 1)) {
                int w = (int) width,
                    h = (int) height;
                for(int i = 0; i <= nbMipmapsLvl; ++i) {
                    if(pxlFormat.isCompressed())
                        gl.glCompressedTexSubImage2D(GL.GL_TEXTURE_2D, i, 0, 0, w, h, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), _data);
                    else
                    	gl.glTexSubImage2D(GL.GL_TEXTURE_2D, i, 0, 0, w, h, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), _data);
                    if(w > 1) w /= 2;
                    if(h > 1) h /= 2;
                }
            } else {
                if(pxlFormat.isCompressed())
                    gl.glCompressedTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) width, (int) height, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), _data);
                else
                    gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) width, (int) height, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), _data);
            }

            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        }
    }
    
    @Override
    public void       bind(StreamHandler2D _stream ) {
    	
    }
    
    @Override
    public ByteBuffer getCopy() {
        int size = (int) (width * height * 4);

        ByteBuffer buffer = ByteBuffer.allocate(size);
        return buffer;
    }

	public void setWraps(int _wrap_s, int _wrap_t) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, glId);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, _wrap_s);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, _wrap_t);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}
	public void setFilters(int _minFilter, int _magFilter) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, glId);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, _minFilter);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, _magFilter);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	private void      autoConfigure(Dimension2D _size, PixelFormat _format, ByteBuffer _data) {
		int w = (int) _size.getWidth(),
			h = (int) _size.getHeight();
		
		boolean   powerOf2     = (w == MipMapUtils.nearestPowerOfTwo(w) && h == MipMapUtils.nearestPowerOfTwo(h));
		
        boolean   canNPowOf2   = gx.hasCapability(Capability.CAP_TEX_NON_POWER_2);
        boolean   canMipmaps   = gx.hasCapability(Capability.CAP_HW_MIPMAPPING) && !powerOf2 ? canNPowOf2 : true;
        boolean   isCompressed = _format.isCompressed();
        
        int       nbMipmaps    = MipMapUtils.getMipLevelsCount(w, h);
        int       width        = w;
        int       height       = h;

        setWraps(GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE);			// GL_CLAMP_TO_EDGE - GL_REPEAT
        setFilters(GL.GL_LINEAR, GL.GL_LINEAR);						// GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, glId);

        if( ! autoMipmaps || ! canMipmaps ) {
            hasMipmaps = false;
            nbMipmapsLvl = 0;
        } else {
            hasMipmaps = true;
            nbMipmapsLvl = nbMipmaps;

        	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL,  nbMipmapsLvl);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_GENERATE_MIPMAP, 	GL.GL_TRUE);
        }

        for(int i = 0; i <= nbMipmapsLvl; ++i) {
            if(isCompressed)
                gl.glCompressedTexImage2D(GL.GL_TEXTURE_2D, i, 0, width, height, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), _data);
            else
            	gl.glTexImage2D(GL.GL_TEXTURE_2D, i, GLEnums.getInternalFormat(pxlFormat), width, height, 0, GLEnums.getPixelFormat(pxlFormat), GLEnums.getPixelType(pxlFormat), _data);

            if(width > 1)  width  /= 2;
            if(height > 1) height /= 2;
        }
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

    @Override
    public  long			getWidth(/*int _unit*/) {
    	return width;
    }
    @Override
    public  long			getHeight(/*int _unit*/) {
    	return height;
    }
    @Override
    public PixelFormat		getPixelFormat() {
        return pxlFormat;
    }

    @Override
    public void				setKeyColor(Integer _key) {
        keyColor = _key;
    }
    @Override
    public Integer			getKeyColor() {
        return keyColor;
    }

    @Override
    public boolean			hasMipmaps() {
        return hasMipmaps;
    }
    @Override
    public boolean			autoMipmaps()  {
        return autoMipmaps;
    }
    @Override
    public int				getMipmapsLevels() {
        return nbMipmapsLvl;
    }

	public void				setActiveFrame(FrameType _frame) {
		activeFrame = _frame;
	}

}
