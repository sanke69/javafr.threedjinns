package fr.threedijnns.gl.rendering;

import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLStream;

public final class GLRenderStreamPBO extends AbstractRenderStreamPBO {
	private final boolean USE_COPY_BUFFER_SUB_DATA;

	public GLRenderStreamPBO(final StreamHandler2D handler, final int samples, final int transfersToBuffer, final ReadbackType readbackType) {
		super(handler, samples, transfersToBuffer, readbackType);

		final GLContextCapabilities caps = GLContextCapabilities.get();
		USE_COPY_BUFFER_SUB_DATA = (caps.OpenGL31 || caps.GL_ARB_copy_buffer);

		System.out.println(String.format("%s created: msaa %d, %s, copy-buf=%s", this.getClass().getSimpleName(), samples, readbackType, USE_COPY_BUFFER_SUB_DATA));
	}

	protected void pinBuffer(final int index) {
    	pinnedBuffers[index] = pbos[index].lock(0, pbos[index].getLength(), LockFlag.ReadOnly);
		assert (pinnedBuffers[index] != null);
	}
	protected void unpinBuffer(final int index) {
		pbos[index].unlock();
		pinnedBuffers[index] = null;
	}

	protected void copyFrames(final int src, final int trg) {
		if (USE_COPY_BUFFER_SUB_DATA)
			GLBuffer.PBO.copyTo(pbos[src], pbos[trg], 0, 0, height * stride);
		else {
			pinnedBuffers[src] = pbos[src].lock(0, pbos[src].getLength(), LockFlag.ReadOnly);
			GLBuffer.PBO.copyTo(pinnedBuffers[src], pbos[trg], 0, pinnedBuffers[src].remaining());
			pbos[src].unlock();
		}
	}

	public static final GLStream.RenderFactory FACTORY = new GLStream.RenderFactory("Asynchronous PBO") {

		public boolean isSupported() {
			final GLContextCapabilities caps = GLContextCapabilities.get();
			return caps.OpenGL21 || caps.GL_ARB_pixel_buffer_object || caps.GL_EXT_pixel_buffer_object;
		}

		public IFrameStream create(final StreamHandler2D handler, final int samples, final int transfersToBuffer) {
			final GLContextCapabilities caps = GLContextCapabilities.get();
			return new GLRenderStreamPBO(handler, samples, transfersToBuffer,
					// Detect NVIDIA and use GetTexImage instead of ReadPixels
					//gx.isNVIDIA() ? ReadbackType.GET_TEX_IMAGE : 
						ReadbackType.READ_PIXELS);
		}

	};

}