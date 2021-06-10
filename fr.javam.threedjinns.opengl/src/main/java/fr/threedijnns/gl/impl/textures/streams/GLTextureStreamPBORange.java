package fr.threedijnns.gl.impl.textures.streams;

import static com.jogamp.opengl.GL.GL_MAP_UNSYNCHRONIZED_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL2ES2.GL_STREAM_DRAW;
import static com.jogamp.opengl.GL2ES3.GL_PIXEL_UNPACK_BUFFER;
import static com.jogamp.opengl.GL3ES3.GL_SYNC_GPU_COMMANDS_COMPLETE;
import static fr.threedijnns.gl.JOGL.gl;

import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.GLStream;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public class GLTextureStreamPBORange extends AbstractTextureStreamPBO {

	private final long[] fences;

	public GLTextureStreamPBORange(final GLTexture2D _texture, final StreamHandler2D _handler,
			final int _transfersToBuffer) {
		super(_texture, _handler, _transfersToBuffer);

		fences = new long[this.transfersToBuffer];
		System.out.println(this.getClass().getSimpleName() + ": created");
	}

	public void pinBuffer(final int index) {
		if (fences[index] != 0) // Wait for TexSubImage to complete
			GLStream.Fence.waitOnFence(fences, index);

//		pbos[index].glBind(GL_PIXEL_UNPACK_BUFFER);
		GLRenderer.glBind(pbos[index], true);
		// gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbos[index]);
		// gl.glBufferData(GL_PIXEL_UNPACK_BUFFER, height * stride, GL_STREAM_DRAW); // Orphan previous buffer
		gl.glBufferData(GL_PIXEL_UNPACK_BUFFER, height * stride, null, GL_STREAM_DRAW); // Orphan previous buffer
		// pinnedBuffers[index] = gl.glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, height * stride, GL_MAP_WRITE_BIT | GL_MAP_UNSYNCHRONIZED_BIT, pinnedBuffers[index]);
		pinnedBuffers[index] = gl.glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, height * stride, GL_MAP_WRITE_BIT | GL_MAP_UNSYNCHRONIZED_BIT);
		gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
	}

	protected void postUpload(final int index) {
		fences[index] = gl.glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
	}

	protected void postProcess(final int index) {
		gl.glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
	}

	public void destroy() {
		destroyObjects();
	}

	public static final GLStream.TextureFactory FACTORY = new GLStream.TextureFactory("ARB_map_buffer_range") {

		public boolean isSupported() {
			GLContextCapabilities caps = GLContextCapabilities.get();
			return GLTextureStreamPBO.FACTORY.isSupported() && (caps.OpenGL30 || caps.GL_ARB_map_buffer_range);
		}

		public TextureStream create(final GLTexture2D _texture, final StreamHandler2D handler,
				final int transfersToBuffer) {
			return new GLTextureStreamPBORange(null, handler, transfersToBuffer);
		}

	};

}