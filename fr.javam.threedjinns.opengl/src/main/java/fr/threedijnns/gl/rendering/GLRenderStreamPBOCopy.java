package fr.threedijnns.gl.rendering;

import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLStream;

public final class GLRenderStreamPBOCopy extends AbstractRenderStreamPBO {

	private GLBuffer devicePBO;

	public GLRenderStreamPBOCopy(final StreamHandler2D handler, final int samples, final int transfersToBuffer, final ReadbackType readbackType) {
		super(handler, samples, transfersToBuffer, readbackType);
		System.out.println(String.format("%s created: msaa x%d, %s", this.getClass().getSimpleName(), samples, readbackType));
	}

	protected void pinBuffer(final int index) {
    	pinnedBuffers[index] = pbos[index].lock(0, pbos[index].getLength(), LockFlag.ReadOnly);
	}
	protected void unpinBuffer(final int index) {
		pbos[index].unlock();
	}

	protected void copyFrames(final int src, final int trg) {
		GLBuffer.PBO.copyTo(pbos[src], pbos[trg], 0, 0, height * stride);
	}

	protected void resizeBuffers(final int height, final int stride) {
		super.resizeBuffers(height, stride);

		devicePBO = new GLBuffer(BufferNature.PixelBufferPack);
		GLBuffer.PBO.allocate(devicePBO, height * stride, GLBuffer.Mode.COPY);
	}

	protected void destroyObjects() {
		if (devicePBO != null)
			devicePBO.destroy();
		super.destroyObjects();
	}

	protected void readBack(final int index) {
		super.readBack(devicePBO);
		GLBuffer.PBO.copyTo(devicePBO, pbos[index], 0, 0, height * stride);
	}

	public static final GLStream.RenderFactory FACTORY = new GLStream.RenderFactory("ARB_copy_buffer") {

		public boolean isSupported() {
			GLContextCapabilities caps = GLContextCapabilities.get();
			return GLRenderStreamPBO.FACTORY.isSupported() 
						&& caps.GL_ARB_copy_buffer
// 						&& caps.GL_NV_gpu_program5 								// Nvidia only
// 						&& (caps.OpenGL40 || caps.GL_ARB_tessellation_shader) 	// Fermi+
						;
		}

		public IFrameStream create(final StreamHandler2D handler, final int samples, final int transfersToBuffer) {
			return new GLRenderStreamPBOCopy(handler, samples, transfersToBuffer, ReadbackType.READ_PIXELS);
		}

	};

}
