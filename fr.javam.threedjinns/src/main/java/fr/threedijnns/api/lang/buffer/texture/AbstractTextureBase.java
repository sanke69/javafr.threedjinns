package fr.threedijnns.api.lang.buffer.texture;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.enums.PixelFormat;
import fr.threedijnns.api.lang.buffer.AbstractBufferBase;
import fr.threedijnns.api.lang.enums.FrameType;
import fr.threedijnns.api.lang.enums.TextureFlag;

public abstract class AbstractTextureBase extends AbstractBufferBase implements TextureBase {
    protected long			width, height;
    protected PixelFormat	format;

    protected Integer		keyColor;

    protected boolean       m_HasMipmaps;
    protected boolean       m_AutoMipmaps;
    protected int         	m_LvlMipmaps;

    protected FrameType 	m_ActiveFrame;

	public AbstractTextureBase(int _w, int _h, PixelFormat _pxl_fmt, TextureFlag... _flags) {
		super(Primitives.BYTE, _w * _h * _pxl_fmt.bytesPerPixel());

		width			= _w;
		height			= _h;
		format 			= _pxl_fmt;
		
		keyColor 		= null;
		
		m_HasMipmaps 	= false;
		m_AutoMipmaps 	= true;
		m_LvlMipmaps 	= 0;

		m_ActiveFrame 	= FrameType.NoId;
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
        return format;
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
        return m_HasMipmaps;
    }
    @Override
    public boolean			autoMipmaps()  {
        return m_AutoMipmaps;
    }
    @Override
    public int				getMipmapsLevels() {
        return m_LvlMipmaps;
    }

	public void				setActiveFrame(FrameType _frame) {
		m_ActiveFrame = _frame;
	}

}
