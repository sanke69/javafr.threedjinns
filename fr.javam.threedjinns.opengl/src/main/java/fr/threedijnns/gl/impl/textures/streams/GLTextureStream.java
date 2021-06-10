package fr.threedijnns.gl.impl.textures.streams;

import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public class GLTextureStream implements TextureStream {
	protected GLTexture2D 		texture;
	protected StreamHandler2D 	handler;

	public GLTextureStream(final GLTexture2D _texture, final StreamHandler2D _handler, final int _transfersToBuffer) {
		super();

		texture = _texture;
		handler = _handler;
	}


	@Override
	public StreamHandler2D getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}

	@Override
	public int getWidth() {
		return (int) texture.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) texture.getHeight();
	}


	@Override
	public void snapshot() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

/*
	@Override
	public void snapshot() {
        if (texture.getWidth() != handler.getWidth() || texture.getHeight() != handler.getHeight())
        	; //texture.resize(handler.getWidth(), handler.getHeight());

        int		pxl_sz		= texture.getPixelFormat().nbBytes();
        int		size		= (int) (texture.getWidth() * texture.getHeight() * pxl_sz);

        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.glGetId());

        if(!m_HasPB) {
        	m_Data = ByteBuffer.allocate(size);
            gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) m_Size.getWidth(), (int) m_Size.getHeight(), GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), m_Data);

            m_HasPB = true;
        }

        final int trgPBO = (int) (bufferIndex % transfersToBuffer);

        // Back-pressure. Make sure we never buffer more than <transfersToBuffer> frames ahead.

        if (processingState.get(trgPBO))
            syncUpload(trgPBO);

        pinBuffer(trgPBO);

        // Send the buffer for processing

        processingState.set(trgPBO, true);
        semaphores[trgPBO].acquireUninterruptibly();

        handler.process(
                width, height,
                pinnedBuffers[trgPBO],
                stride,
                semaphores[trgPBO]
        );

        bufferIndex++;

        if (resetTexture) // Synchronize to show the first frame immediately
            syncUpload(trgPBO);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	
	
	
    @Override
	public int  glGetId() {
		return glTextureId;
	}
    @Override
    public void glDestroy() {
    	GLRenderer.glDeleteTextures(glTextureId);
    }

	public void glBind() {
		gl.glBindTexture(GL.GL_TEXTURE_2D, glTextureId);
	}

    @Override
    public void enable(int _unit) {
	    gl.glActiveTexture(GL.GL_TEXTURE0 + _unit);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, glTextureId);
    }
    @Override
    public void disable(int _unit) {
    	gl.glActiveTexture(GL.GL_TEXTURE0 + _unit);

    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    	gl.glDisable(GL.GL_TEXTURE_2D);
    }

    @Override
    public ByteBuffer lock(long _offset, long _size, LockFlag _flags) {
        if(m_KeepPB && m_HasPB)
            return m_Data;

        int		pxl_sz		= GLEnums.getBytesPerPixel(m_Format);
        int		size		= (int) (m_Size.getWidth() * m_Size.getHeight() * pxl_sz);

        gl.glBindTexture(GL.GL_TEXTURE_2D, glTextureId);

        if(!m_HasPB) {
        	m_Data = ByteBuffer.allocate(size);
            gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) m_Size.getWidth(), (int) m_Size.getHeight(), GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), m_Data);

            m_HasPB = true;
        }

        return m_Data;
	}
    @Override
    public void       unlock() {
	    if(!m_HasPB)
            return ;

        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) m_Size.getWidth(), (int) m_Size.getHeight(), GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), m_Data);

        if(!m_KeepPB) {
            m_HasPB = false;
            m_Data  = null;
        }

        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}
    @Override
    public void       update(long _offset, long _size, LockFlag _flags, Buffer _data) {
	    if(_data != null && _size >= m_Size.getWidth() * m_Size.getHeight()) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, glTextureId);

            if(m_AutoMipmaps || (m_HasMipmaps && m_LvlMipmaps > 1)) {
                int w = (int) m_Size.getWidth(),
                    h = (int) m_Size.getHeight();
                for(int i = 0; i <= m_LvlMipmaps; ++i) {
                    if(m_Format.isCompressed())
                        gl.glCompressedTexSubImage2D(GL.GL_TEXTURE_2D, i, 0, 0, w, h, GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), _data);
                    else
                    	gl.glTexSubImage2D(GL.GL_TEXTURE_2D, i, 0, 0, w, h, GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), _data);
                    if(w > 1) w /= 2;
                    if(h > 1) h /= 2;
                }
            } else {
                if(m_Format.isCompressed())
                    gl.glCompressedTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) m_Size.getWidth(), (int) m_Size.getHeight(), GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), _data);
                else
                    gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, (int) m_Size.getWidth(), (int) m_Size.getHeight(), GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), _data);
            }

            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        }
    }
    
    @Override
    public void       bind(StreamHandler2D _stream ) {
    	
    }
    
    @Override
    public ByteBuffer getCopy() {
        int size = (int) (m_Size.getWidth() * m_Size.getHeight() * 4);

        ByteBuffer buffer = ByteBuffer.allocate(size);
        return buffer;
    }

	public void setFilters(int _minFilter, int _magFilter) {
        gl.glBindTexture(GL_TEXTURE_2D, glTextureId);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, _minFilter);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, _magFilter);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
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

        gl.glBindTexture(GL.GL_TEXTURE_2D, glTextureId = GLRenderer.newTextureID());

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);					//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);					//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR

        if( ! m_AutoMipmaps || ! canMipmaps ) {
            m_HasMipmaps = false;
            m_LvlMipmaps = 0;
        } else {
            m_HasMipmaps = true;
            m_LvlMipmaps = nbMipmaps;

        	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL,  m_LvlMipmaps);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_GENERATE_MIPMAP, 	GL.GL_TRUE);
        }

        for(int i = 0; i <= m_LvlMipmaps; ++i) {
            if(isCompressed)
                gl.glCompressedTexImage2D(GL.GL_TEXTURE_2D, i, 0, width, height, GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), _data);
            else
            	gl.glTexImage2D(GL.GL_TEXTURE_2D, i, GLEnums.getInternalFormat(m_Format), width, height, 0, GLEnums.getPixelFormat(m_Format), GLEnums.getPixelType(m_Format), _data);

            if(width > 1)  width  /= 2;
            if(height > 1) height /= 2;
        }
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}
*/
}
