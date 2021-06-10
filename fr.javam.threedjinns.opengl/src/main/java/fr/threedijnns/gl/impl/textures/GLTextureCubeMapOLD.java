package fr.threedijnns.gl.impl.textures;

import static fr.threedijnns.gl.JOGL.gl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import fr.java.lang.enums.PixelFormat;
import fr.java.lang.exceptions.NotYetImplementedException;
import fr.java.math.geometry.plane.Dimension2D;
import fr.java.maths.geometry.plane.types.SimpleDimension2D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.texture.AbstractTextureBase;
import fr.threedijnns.api.lang.enums.Capability;
import fr.threedijnns.api.lang.enums.FrameType;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.enums.TextureFlag;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.api.utils.MipMapUtils;
import fr.threedijnns.gl.impl.GLEnums;

public class GLTextureCubeMapOLD extends AbstractTextureBase {
	int					m_Texture;          ///<Identifiant OpenGL de la texture>
	int					m_PBO;              ///<Identifiant OpenGL du PBO>

    public GLTextureCubeMapOLD(int _w, int _h, int _pxl_size, PixelFormat _format, ByteBuffer _data, TextureFlag... _flags) {
    	super(_w, _h, _format, _flags);

	    if(_flags != null && Arrays.asList(_flags).contains(TextureFlag.TEX_NOMIPMAP))
	    	m_AutoMipmaps = false;

	    autoGenerateMipmaps(SimpleDimension2D.of((int) width, (int) height), _format, _data);
	}

	@Override
	public void enable(int _unit) {
        gl.glEnable(GL2.GL_TEXTURE_CUBE_MAP);

        gl.glActiveTexture(GL.GL_TEXTURE0 + _unit);

    	gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, m_Texture);

        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S,     GL.GL_REPEAT);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T,     GL.GL_REPEAT);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);			//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);			//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR

        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_REFLECTION_MAP);		// GL_REFLECTION_MAP - GL_NORMAL_MAP
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_REFLECTION_MAP);		// GL_REFLECTION_MAP - GL_NORMAL_MAP
        gl.glTexGeni(GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_REFLECTION_MAP);		// GL_REFLECTION_MAP - GL_NORMAL_MAP

        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glEnable(GL2.GL_TEXTURE_GEN_R);

	}
	@Override
	public void disable(int _unit) {
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);
        gl.glDisable(GL2.GL_TEXTURE_GEN_R);

        gl.glBindTexture(GL2.GL_TEXTURE_CUBE_MAP, 0);

        gl.glActiveTexture(GL.GL_TEXTURE0 + _unit);

        gl.glDisable(GL2.GL_TEXTURE_CUBE_MAP);
    }

	@Override
	public ByteBuffer lock(long _offset, long _size, LockFlag _flag) {return null;
		/*
        if(m_KeepPB && m_HasPB)
            return m_Data;

        PixelFormat texFmt     = m_Format;
        int          pxl_sz     = GLEnums.getBytesPerPixel(m_Format);
        int          size       = (int) (m_Width * m_Height * pxl_sz);
        boolean      canUsePBO  = gx.hasCapability(Capability.CAP_PBO);
        int          GL_FRAMEID = GLEnums.get(m_ActiveFrame);

        gl.glBindTexture(GL2.GL_TEXTURE_CUBE_MAP, m_Texture);

        if(m_UsePBO) {
            if(!canUsePBO) {
                m_UsePBO = false;
                return lock(_offset, _size, _flag);
            }
            if(!m_HasPB) {
            	IntBuffer pboID = IntBuffer.allocate(1);
        		gl.glGenBuffers(1, pboID);
        		m_PBO = pboID.get(0);

                // Creation
                gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, m_PBO);
                gl.glBufferData(GL2.GL_PIXEL_UNPACK_BUFFER, size, null, GL2.GL_STREAM_DRAW);
                m_Data = gl.glMapBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, GL2.GL_WRITE_ONLY);

                m_HasPB = true;
            }
        } else {
            if(!m_HasPB) {
            	m_Data = ByteBuffer.allocate(size);
                gl.glTexSubImage2D(GL_FRAMEID, 0, 0, 0, (int) m_Width, (int) m_Height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), m_Data);

                m_HasPB = true;
            }
        }

        return m_Data;
        */
	}
	@Override
	public void unlock() {
		/*
	    if(!m_HasPB)
            return ;

        PixelFormat texFmt     = m_Format;
        int          GL_FRAMEID = GLEnums.get(m_ActiveFrame);

        if(m_UsePBO) {
            gl.glUnmapBuffer(GL2.GL_PIXEL_UNPACK_BUFFER);
            gl.glTexSubImage2D(GL_FRAMEID, 0, 0, 0, (int) m_Width, (int) m_Height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), 0);
            gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);

            if(!m_KeepPB) {
            	IntBuffer pboIDs = IntBuffer.wrap(new int[] { m_PBO });
                gl.glDeleteBuffers(1, pboIDs);
                m_Data = null;
            }

            m_HasPB = false;
        } else {
            gl.glTexSubImage2D(GL_FRAMEID, 0, 0, 0, (int) m_Width, (int) m_Height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), m_Data);

            if(!m_KeepPB) {
                m_HasPB = false;
                m_Data  = null;
            }
        }

        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        */
	}
	
	@Override
	public void update(long _offset, long _size, LockFlag _flags, Buffer _data) {
	    if(m_ActiveFrame == FrameType.NoId)
            return ;

        int GL_FRAMEID = GLEnums.get(m_ActiveFrame);

	    if(_data != null && _size >= width * height) {
            gl.glBindTexture(GL2.GL_TEXTURE_CUBE_MAP, m_Texture);

            if(m_AutoMipmaps || (m_HasMipmaps && m_LvlMipmaps > 1)) {
                int w = (int) width,
                    h = (int) height;
                for(int i = 0; i <= m_LvlMipmaps; ++i) {
                    if(format.isCompressed())
                        gl.glCompressedTexSubImage2D(GL_FRAMEID, i, 0, 0, w, h, GLEnums.getPixelFormat(format), GLEnums.getPixelType(format), _data);
                    else
                        gl.glTexSubImage2D(GL_FRAMEID, i, 0, 0, w, h, GLEnums.getPixelFormat(format), GLEnums.getPixelType(format), _data);
                    if(w > 1) w /= 2;
                    if(h > 1) h /= 2;
                }
            } else {
                if(format.isCompressed())
                    gl.glCompressedTexSubImage2D(GL_FRAMEID, 0, 0, 0, (int) width, (int) height, GLEnums.getPixelFormat(format), GLEnums.getPixelType(format), _data);
                else
                    gl.glTexSubImage2D(GL_FRAMEID, 0, 0, 0, (int) width, (int) height, GLEnums.getPixelFormat(format), GLEnums.getPixelType(format), _data);
            }

            gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, 0);
        }
	}
    public ByteBuffer getCopy() {
        PixelFormat texFmt = format;
        int         size   = (int) (width * height * 4);

        byte[] buffer = new byte[size];
        return ByteBuffer.wrap(buffer);
    }

	private void          autoGenerateMipmaps(Dimension2D _size, PixelFormat _format, ByteBuffer _data) {
        int			width      = (int) _size.getWidth();
        int			height     = (int) _size.getHeight();
        boolean		powerOf2   = (width == MipMapUtils.nearestPowerOfTwo(width) && height == MipMapUtils.nearestPowerOfTwo(height));
		boolean		canMipmaps = gx.hasCapability(Capability.CAP_HW_MIPMAPPING);
        int			nbMipmaps  = MipMapUtils.getMipLevelsCount(width, height);
        PixelFormat	texFmt     = _format;
        int			pxl_sz     = GLEnums.getBytesPerPixel(_format);

    	IntBuffer texID = IntBuffer.allocate(1);
		gl.glGenTextures(1, texID);
		m_Texture = texID.get(0);

		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, m_Texture);
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);			//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);			//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR

/**/
        if(powerOf2) {

            if(m_AutoMipmaps && canMipmaps) {
            	gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_MAX_LEVEL,    nbMipmaps);
            	gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_GENERATE_MIPMAP, GL.GL_TRUE);

                for(int i = 0; i <= nbMipmaps; ++i) {
                    if(_format.isCompressed()) {
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, i, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, i, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, i, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, i, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, i, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, i, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                   } else {
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, i, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, i, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, i, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, i, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, i, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, i, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                   }
                    if(width > 1)  width  /= 2;
                    if(height > 1) height /= 2;
                }
                m_HasMipmaps = true;
                m_LvlMipmaps = nbMipmaps;
                
                
                
                
                
                
                
                
            } else {
                if(_data != null)
                    if(_format.isCompressed()) {
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    } else {
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    }
                else {
                    byte[] buffer = new byte[width * height * pxl_sz];          
                    ByteBuffer data = ByteBuffer.wrap(buffer);                  
                    if(_format.isCompressed()) {
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                   } else {
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
                   }
                }
            }
        } else {
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,     GL.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,     GL.GL_REPEAT);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            if(_data != null) {
                if(_format.isCompressed()) {
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                } else {
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
                }
            } else {
                byte[] buffer = new byte[width * height * pxl_sz];
                ByteBuffer data = ByteBuffer.wrap(buffer);
                if(_format.isCompressed()) {
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);     
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);     
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);     
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);     
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);     
                    gl.glCompressedTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, width, height, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);     
                } else {
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);           
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);           
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);           
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);           
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);           
                    gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);           
                }

            }
        }

        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP - GL_CLAMP_TO_EDGE - GL_REPEAT
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP - GL_CLAMP_TO_EDGE - GL_REPEAT
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_R,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP - GL_CLAMP_TO_EDGE - GL_REPEAT
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);	            //GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);		        //GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR

        byte[] data = new byte[(int) (_size.getWidth() * _size.getHeight() * pxl_sz)];
        for(int i = 0; i < _size.getHeight(); i++) {
            for(int j = 0; j < _size.getWidth(); j++) {
                if(i < _size.getHeight()/2) {
                    data[(int) (3*(i*_size.getWidth()+j))]   = 0;
                } else {
                    data[(int) (3*(i*_size.getWidth()+j))]   = (byte) 255;
                }
                if(j < _size.getWidth()/2) {
                    data[(int) (3*(i*_size.getWidth()+j)+2)] = 0;
                } else {
                    data[(int) (3*(i*_size.getWidth()+j)+2)] = (byte) 255;
                }
                data[(int) (3*(i*_size.getWidth()+j)+1)] 	 = (byte) 128;
            }
        }
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);

        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_S,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP - GL_CLAMP_TO_EDGE - GL_REPEAT
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_T,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP - GL_CLAMP_TO_EDGE - GL_REPEAT
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_R,     GL.GL_CLAMP_TO_EDGE);			//GL_CLAMP - GL_CLAMP_TO_EDGE - GL_REPEAT
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);	            //GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
		gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);		        //GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
/**/
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,     GL.GL_REPEAT);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,     GL.GL_REPEAT);			//GL_CLAMP_TO_EDGE - GL_REPEAT
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);			//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);			//GL_NEAREST - GL_LINEAR - GL_LINEAR_MIPMAP_LINEAR
/*
        if(_data != null) {
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), _data);
        } else {
//        	byte[] buffer = new byte[width * height * pxl_sz];
        	byte[] buffer = new byte[(int) (_size.getWidth() * _size.getHeight() * pxl_sz)];
        	for(int i = 0; i < (int) (_size.getWidth() * _size.getHeight() * pxl_sz); ++i)
        		buffer[i] = (byte) (Math.random() * 255);
            ByteBuffer data = ByteBuffer.wrap(buffer);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GLEnums.getInternalFormat(texFmt), width, height, 0, GLEnums.getPixelFormat(texFmt), GLEnums.getPixelType(texFmt), data);
        }
/**/
        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, 0);
	}

	@Override
	public void bind(StreamHandler2D _handler) {
		throw new NotYetImplementedException();
	}
}
