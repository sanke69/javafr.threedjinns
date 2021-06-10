package fr.threedijnns.gl.rendering;

import static com.jogamp.opengl.GL2GL3.GL_EXTERNAL_VIRTUAL_MEMORY_BUFFER_AMD;
import static com.jogamp.opengl.GL3ES3.GL_SYNC_GPU_COMMANDS_COMPLETE;
import static fr.threedijnns.gl.JOGL.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import fr.java.utils.primitives.Buffers;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.GLStream;

public final class GLRenderStreamPBOonAMD extends AbstractRenderStreamPBO {

	private final long[] fences;

	public GLRenderStreamPBOonAMD(final StreamHandler2D handler, final int samples, final int transfersToBuffer) {
		super(handler, samples, transfersToBuffer, ReadbackType.READ_PIXELS);

		fences = new long[this.transfersToBuffer];
		System.out.println(String.format("%s created: msaa %d, %s", this.getClass().getSimpleName(), samples, ReadbackType.READ_PIXELS));
	}

	protected void resizeBuffers(final int height, final int stride) {
		final int renderBytes = height * stride;

		for (int i = 0; i < pbos.length; i++) {
			// Pre-allocate page-aligned pinned buffers
			final int PAGE_SIZE = GLStream.PageSizeProvider.PAGE_SIZE;

			final ByteBuffer buffer = Buffers.allocateByteBuffer(renderBytes + PAGE_SIZE);
			// WTF rely on native memory position???
			/*
			 * final int pageOffset = (int)(MemoryUtil.getAddress(buffer) % PAGE_SIZE);
			 */
			final int pageOffset = 0;
			buffer.position(PAGE_SIZE - pageOffset); // Aligns to page
			buffer.limit(buffer.capacity() - pageOffset); // Caps remaining() to
															// renderBytes

			pinnedBuffers[i] = buffer.slice().order(ByteOrder.nativeOrder());

			pbos[i] = new GLBuffer(GL_EXTERNAL_VIRTUAL_MEMORY_BUFFER_AMD);
			GLBuffer.PBO.allocate(pbos[i], pinnedBuffers[i].remaining(), GLBuffer.Mode.READ, pinnedBuffers[i]);

		}

		GLRenderer.glUnbind(GL_EXTERNAL_VIRTUAL_MEMORY_BUFFER_AMD);
	}

	protected void pinBuffer(final int index) {
		if (fences[index] != 0) // Wait for ReadPixels on the PBO to complete
			GLStream.Fence.waitOnFence(fences, index);
	}
	protected void unpinBuffer(final int index) {
	}

	protected void readBack(final int index) {
		super.readBack(pbos[index]);

		// Insert a fence after ReadPixels
		fences[index] = gl.glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
	}
	protected void copyFrames(final int src, final int trg) {
		GLStream.Fence.waitOnFence(fences, src);

		final ByteBuffer srcBuffer = pinnedBuffers[src];
		final ByteBuffer trgBuffer = pinnedBuffers[trg];

		trgBuffer.put(srcBuffer);

		trgBuffer.flip();
		srcBuffer.flip();
	}

	protected void destroyObjects() {
		for (int i = 0; i < fences.length; i++) {
			if (fences[i] != 0)
				GLStream.Fence.waitOnFence(fences, i);
		}

		super.destroyObjects();
	}

	public static final GLStream.RenderFactory FACTORY = new GLStream.RenderFactory("AMD_pinned_memory") {

		public boolean isSupported() {
			GLContextCapabilities caps = GLContextCapabilities.get();
			return caps.GL_AMD_pinned_memory && (caps.OpenGL32 || caps.GL_ARB_sync);
		}

		public IFrameStream create(final StreamHandler2D handler, final int samples, final int transfersToBuffer) {
			return new GLRenderStreamPBOonAMD(handler, samples, transfersToBuffer);
		}

	};

}