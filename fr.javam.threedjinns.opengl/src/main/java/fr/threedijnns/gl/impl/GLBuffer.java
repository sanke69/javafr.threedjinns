package fr.threedijnns.gl.impl;

import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_RENDERBUFFER;
import static com.jogamp.opengl.GL.GL_RGBA;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2ES2.GL_UNPACK_ROW_LENGTH;
import static com.jogamp.opengl.GL2ES3.GL_COPY_WRITE_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_PIXEL_PACK_BUFFER;
import static fr.threedijnns.gl.JOGL.gl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Optional;

import com.jogamp.opengl.GL2;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.enums.PixelFormat;
import fr.java.lang.exceptions.NotYetImplementedException;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.threedijnns.api.lang.buffer.AbstractBufferBase;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.stream.StreamBuffered2D;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public class GLBuffer extends AbstractBufferBase {
	
	public static enum Mode { READ(GL2.GL_STREAM_READ), COPY(GL2.GL_STREAM_COPY), DRAW(GL2.GL_STREAM_DRAW); int glEnum; private Mode(int _mode) { glEnum = _mode; } };

	protected int 		glId;
	BufferNature  		nature;
	Optional<Integer> 	customNature;

	public GLBuffer(BufferNature _nature) {
		super(Primitives.BYTE, 0);
        glId   			= GLRenderer.glGenBuffer(_nature);
        nature 			= _nature;
        customNature 	= Optional.empty();
	}
	public GLBuffer(int _customNature) {
		super(Primitives.BYTE, 0);
        glId   			= GLRenderer.glGenBuffer(BufferNature.PixelBufferPack);
        nature 			= BufferNature.CustomBuffer;
        customNature 	= Optional.of(_customNature);
	}
	public GLBuffer(BufferNature _nature, Primitives _type, long _count) {
		super(_type, _count);
        glId   			= GLRenderer.glGenBuffer(_nature);
        nature 			= _nature;
        customNature 	= Optional.empty();
	}

	public int          getId() {
		return glId;
	}
	public BufferNature getNature() {
		return nature;
	}
	public int 			getCustomNature() {
		return customNature.get();
	}

	public void 		setCustomNature(int _customNature) {
		assert(nature == BufferNature.CustomBuffer);

		nature       = BufferNature.CustomBuffer;
        customNature = Optional.of(_customNature);
	}

	@Override
	public Buffer 		getCopy() {
		ByteBuffer bb    = lock(0, getLength(), LockFlag.ReadOnly);
		Buffer     clone = null;

		switch(primitive) {
		case BYTE:		clone = ByteBuffer.allocate(bb.capacity());
						((ByteBuffer) clone).put(bb);
						break;
		case SHORT:		clone = ShortBuffer.allocate(bb.capacity() / Primitives.SHORT.nbBytes());
						((ShortBuffer) clone).put(bb.asShortBuffer());
						break;
		case INTEGER:	clone = IntBuffer.allocate(bb.capacity() / Primitives.INTEGER.nbBytes());
						((IntBuffer) clone).put(bb.asIntBuffer());
						break;
		case LONG:		clone = LongBuffer.allocate(bb.capacity() / Primitives.LONG.nbBytes());
						((LongBuffer) clone).put(bb.asLongBuffer());
						break;
		case FLOAT:		clone = FloatBuffer.allocate(bb.capacity() / Primitives.FLOAT.nbBytes());
						((FloatBuffer) clone).put(bb.asFloatBuffer());
						break;
		case DOUBLE:	clone = DoubleBuffer.allocate(bb.capacity() / Primitives.DOUBLE.nbBytes());
						((DoubleBuffer) clone).put(bb.asDoubleBuffer());
						break;
		default:
		case UNDEF: 	throw new IllegalArgumentException();
		}

		unlock();
		return clone; 
	}

	@Override
	public ByteBuffer 	lock(long _offset, long _size, LockFlag _flags) {
		int pboTarget = GLEnums.get(nature) != -1 ? GLEnums.get(nature) : customNature.get();
		gl.glBindBuffer(pboTarget, glId);


		ByteBuffer buffer = gl.glMapBuffer(pboTarget, GLEnums.get(_flags));

		if(buffer == null)
			return null;

		buffer.position((int) _offset * primitive.nbBytes());
		return buffer;
	}
	@Override
	public void       	unlock() {
		int pboTarget = GLEnums.get(nature) != -1 ? GLEnums.get(nature) : customNature.get();

		gl.glUnmapBuffer(pboTarget);
	}
	
	@Override
	public void       	update(long _size, long _stride, LockFlag _flags, Buffer _data) {}

	public void 		destroy() {
		GLRenderer.glDeleteBuffer(nature, glId);
	}



	public static class RenderBuffer {

		public static void allocate(GLBuffer _buffer, final int width, final int height, final PixelFormat _format, final int samples) {
			assert(_buffer.nature == BufferNature.RenderBuffer);
			
			gl.glBindRenderbuffer(GL_RENDERBUFFER, _buffer.glId);
			if (samples <= 1)
				gl.glRenderbufferStorage(GL_RENDERBUFFER, GLEnums.getInternalFormat(_format), width, height);
			else
				gl.glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GLEnums.getInternalFormat(_format), width, height);
			gl.glBindRenderbuffer(GL_RENDERBUFFER, 0);
		}

	}

	public static class PBO {

		public static final void allocate(GLBuffer _buffer, int _size, GLBuffer.Mode _mode) {
			assert(_buffer.nature == BufferNature.PixelBufferPack || _buffer.nature == BufferNature.PixelBufferUnpack || _buffer.nature == BufferNature.CustomBuffer);

			_buffer.capacity = _size;

			int pboTarget = GLEnums.get(_buffer.nature) != -1 ? GLEnums.get(_buffer.nature) : _buffer.customNature.get();
			int pboUsage  = _mode.glEnum;

			gl.glBindBuffer(pboTarget, _buffer.glId);
			gl.glBufferData(pboTarget, _size, null, pboUsage);
			gl.glBindBuffer(pboTarget, 0);
		}
		public static final void allocate(GLBuffer _buffer, int _size, GLBuffer.Mode _mode, Buffer _source) {
			assert(_buffer.nature == BufferNature.PixelBufferPack || _buffer.nature == BufferNature.PixelBufferUnpack || _buffer.nature == BufferNature.CustomBuffer);

			_buffer.capacity = _size;

			int pboTarget = GLEnums.get(_buffer.nature) != -1 ? GLEnums.get(_buffer.nature) : _buffer.customNature.get();
			int pboUsage  = _mode.glEnum;

			gl.glBindBuffer(pboTarget, _buffer.glId);
			gl.glBufferData(pboTarget, _size, _source, pboUsage);
			gl.glBindBuffer(pboTarget, 0);
		}

		public static final void copyTo(Buffer   _src, GLBuffer _dst, int _offset, int _length) {
			assert(_dst.nature == BufferNature.PixelBufferPack || _dst.nature == BufferNature.PixelBufferUnpack || _dst.nature == BufferNature.CustomBuffer);

			gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, _dst.glId);
			gl.glBufferSubData(GL_PIXEL_PACK_BUFFER, _offset, _length, _src);
			gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
		}
		public static final void copyTo(GLBuffer _src, GLBuffer _dst, int _srcOffset, int _dstOffset, int _length) {
			assert(_dst.nature == BufferNature.PixelBufferPack || _dst.nature == BufferNature.PixelBufferUnpack || _dst.nature == BufferNature.CustomBuffer);

			switch(_src.nature) {
			case VertexBuffer:
			case NormalBuffer:
			case ColorBuffer:
			case MapBuffer:	
			case IndexBuffer:		throw new NotYetImplementedException();

			case PixelBufferPack:
			case PixelBufferUnpack:
			case CustomBuffer:		gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, _src.glId);
							    	gl.glBindBuffer(GL_COPY_WRITE_BUFFER, _dst.glId);
					
							        gl.glCopyBufferSubData(GL_PIXEL_PACK_BUFFER, GL_COPY_WRITE_BUFFER, _srcOffset, _dstOffset, _length);
					
									gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
							    	gl.glBindBuffer(GL_COPY_WRITE_BUFFER, 0);
									break;

			case FrameBuffer:
									break;
			case RenderBuffer:
									break;
			case Texture:			boolean     resetTexture = true;
									GLTexture2D texture      = (GLTexture2D) _dst;

									gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, _src.glId);
									gl.glBindTexture(GL_TEXTURE_2D, texture.getId());

							        gl.glPixelStorei(GL_UNPACK_ROW_LENGTH, (int) texture.getStride() >> 2);
							        if (resetTexture)
							            gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (int) texture.getWidth(), (int) texture.getHeight(), 0, GLEnums.getPixelFormat(texture.getPixelFormat()), GLEnums.getPixelType(texture.getPixelFormat()), 0);
							        else
							            gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (int) texture.getWidth(), (int) texture.getHeight(), GLEnums.getPixelFormat(texture.getPixelFormat()), GLEnums.getPixelType(texture.getPixelFormat()), 0);
							        gl.glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);

							        gl.glBindTexture(GL_TEXTURE_2D, 0);
							        gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
									break;
			default:				throw new NotYetImplementedException();
			}

		}

		public static final void copyFrom(GLBuffer _src, ByteBuffer   _dst, int _offset, int _length) {
			assert(_src.nature == BufferNature.PixelBufferPack || _src.nature == BufferNature.PixelBufferUnpack || _src.nature == BufferNature.CustomBuffer);

			ByteBuffer data = _src.lock(0, _src.getLength(), LockFlag.ReadOnly);
			_dst.put(data);
			_src.unlock();
		}
		public static final void copyFrom(GLBuffer _src, GLBuffer _dst, int _srcOffset, int _dstOffset, int _length) {
			assert(_dst.nature == BufferNature.PixelBufferPack || _dst.nature == BufferNature.PixelBufferUnpack || _dst.nature == BufferNature.CustomBuffer);

			switch(_src.nature) {
			case VertexBuffer:
			case NormalBuffer:
			case ColorBuffer:
			case MapBuffer:	
			case IndexBuffer:		throw new NotYetImplementedException();

			case PixelBufferPack:
			case PixelBufferUnpack:
			case CustomBuffer:		gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, _src.glId);
							    	gl.glBindBuffer(GL_COPY_WRITE_BUFFER, _dst.glId);
					
							        gl.glCopyBufferSubData(GL_PIXEL_PACK_BUFFER, GL_COPY_WRITE_BUFFER, _srcOffset, _dstOffset, _length);
					
									gl.glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
							    	gl.glBindBuffer(GL_COPY_WRITE_BUFFER, 0);
									break;

			case FrameBuffer:
									break;
			case RenderBuffer:
									break;
			case Texture:
									break;
			default:				throw new NotYetImplementedException();
			}

		}

	}

	public static class FBO {

		public static final void blit(GLBuffer _src, GLBuffer _dst, SimpleRectangle2D in, SimpleRectangle2D out, int _glBufferBit, int _glOption) {
			assert(_src.nature == BufferNature.FrameBuffer);
			assert(_dst.nature == BufferNature.FrameBuffer);

			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, _src.glId);
			gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, _dst.glId);

			gl.glBlitFramebuffer(	(int) in.getX(), (int) in.getY(), (int) in.getWidth(), (int) in.getHeight(), 
									(int) out.getX(), (int) out.getY(), (int) out.getWidth(), (int) out.getHeight(), 
									_glBufferBit, _glOption);

			gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		}

		public static final void readFrom(GLBuffer _src, GLBuffer _dst, int glAttachment) {
			assert(_src.nature == BufferNature.FrameBuffer);

			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, _src.glId);

			if(_dst.nature == BufferNature.Texture)
				gl.glFramebufferTexture2D(GL_READ_FRAMEBUFFER, glAttachment, GL_TEXTURE_2D, _dst.getId(), 0);
			else if(_dst.nature == BufferNature.RenderBuffer)
				gl.glFramebufferRenderbuffer(GL_READ_FRAMEBUFFER, glAttachment, GL_RENDERBUFFER, _dst.getId());
			else 
				throw new NotYetImplementedException();

			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		}

		public static final void drawTo(GLBuffer _dst, GLBuffer _src, int glAttachment) {
			assert(_dst.nature == BufferNature.FrameBuffer);

			gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, _dst.glId);

			if(_src instanceof GLTexture2D)
				gl.glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, glAttachment, GL_TEXTURE_2D, _src.getId(), 0);
			else if(_src instanceof GLBuffer && _src.nature == BufferNature.RenderBuffer)
				gl.glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, glAttachment, GL_RENDERBUFFER, _src.getId());
			else 
				throw new NotYetImplementedException();

			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		}

	}

	public static abstract class StreamBufferedPBO extends StreamBuffered2D {
		protected final GLBuffer[] pbos;
	
		protected StreamBufferedPBO(final StreamHandler2D handler, final int transfersToBuffer) {
			super(handler, transfersToBuffer);
			pbos = new GLBuffer[transfersToBuffer];
		}

		protected void reallocPBOs(final BufferNature pboTarget, final int height, final int stride, final GLBuffer.Mode pboUsage) {
			final int renderBytes = height * stride;
	
			for(int i = 0; i < pbos.length; i++) {
				pbos[i]          = new GLBuffer(pboTarget);
				pinnedBuffers[i] = null;

				GLBuffer.PBO.allocate(pbos[i], renderBytes, pboUsage);
			}
		}

	}

}
