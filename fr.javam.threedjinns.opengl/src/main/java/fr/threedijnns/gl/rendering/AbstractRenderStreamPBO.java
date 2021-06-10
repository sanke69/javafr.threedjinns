package fr.threedijnns.gl.rendering;

import static com.jogamp.opengl.GL.GL_BGRA;
import static com.jogamp.opengl.GL.GL_COLOR_ATTACHMENT0;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_ATTACHMENT;
import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2ES3.GL_PACK_ROW_LENGTH;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_8_8_8_8_REV;
import static fr.threedijnns.gl.JOGL.gl;

import fr.java.lang.enums.PixelFormat;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public abstract class AbstractRenderStreamPBO extends GLBuffer.StreamBufferedPBO implements IFrameStream {

	public static enum ReadbackType {
		READ_PIXELS,	// RenderBuffers on FBO, ReadPixels to readback.
		GET_TEX_IMAGE	// Textures on FBO, GetTexImage to readback.
	}

	private final ReadbackType 	readbackType;

	protected int 				synchronousFrames;
	private   int 				samples;

	private GLBuffer  			renderFBO;

	private GLBuffer    		colorBuffer;			// Could be Texture2D (GET_TEX_IMAGE) or RenderBuffer (READ_PIXELS)
	private GLBuffer			depthBuffer;

	// Multisample anti-aliasing
	private GLBuffer 			msaaResolveFBO;
	private GLBuffer 			msaaResolveBuffer;		// Could be Texture2D (GET_TEX_IMAGE) or RenderBuffer (READ_PIXELS)
	
	// Fast approximate anti-aliasing
	// ...

	protected AbstractRenderStreamPBO(final StreamHandler2D _handler, int _samples, int _transfersToBuffer, ReadbackType _readbackType) {
		super(_handler, _transfersToBuffer);

		readbackType = _readbackType;
		samples      = GLRenderer.checkSampleCapability(_samples, GLContextCapabilities.get());

		renderFBO    = new GLBuffer(BufferNature.FrameBuffer);
	}

	public StreamHandler2D 	getHandler() {
		return handler;
	}

	protected abstract void copyFrames(final int src, final int trg);

	protected abstract void pinBuffer(final int index);
	protected abstract void unpinBuffer(final int index);
	
    protected void 			postProcess(int index) { unpinBuffer(index); }

	@Override
	public void 			bind() {
		if(width != handler.getWidth() || height != handler.getHeight())
			resize(handler.getWidth(), handler.getHeight());

		GLRenderer.glBind(renderFBO, true);
	}

	@Override
	public void 			swapBuffers() {
		if (width == 0 || height == 0)
			return;

		prepareFramebuffer();

		final int trgPBO = (int) (bufferIndex % transfersToBuffer);
		final int srcPBO = (int) ((bufferIndex - 1) % transfersToBuffer);

		GLRenderer.glBind(pbos[trgPBO], false);

		if(processingState.get(trgPBO))
			waitForProcessingToComplete(trgPBO);

		readBack(pbos[trgPBO]);

		if (0 < synchronousFrames) {
			copyFrames(trgPBO, srcPBO);
			synchronousFrames--;
		}

		pinBuffer(srcPBO);

		processingState.set(srcPBO, true);
		semaphores[srcPBO].acquireUninterruptibly();

		handler.process(width, height, pinnedBuffers[srcPBO], stride, semaphores[srcPBO]);

		bufferIndex++;
	}

	protected void 			prepareFramebuffer() {
		if (msaaResolveFBO == null)
			gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		else
			GLBuffer.FBO.blit(	renderFBO, msaaResolveFBO, 
								new SimpleRectangle2D(0, 0, width, height), 
								new SimpleRectangle2D(0, 0, width, height), 
								GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	private void 			resize(final int _width, final int _height) {
		if (_width < 0 || _height < 0)
			throw new IllegalArgumentException("Invalid dimensions: " + _width + " x " + _height);

		destroyObjects();

		width  = _width;
		height = _height;
		stride = GLRenderer.getStride(_width);

		if(_width == 0 || _height == 0)
			return;

		bufferIndex = synchronousFrames = transfersToBuffer - 1;

		// Setup render FBO
		boolean monoSample = (samples <= 1 && readbackType == ReadbackType.GET_TEX_IMAGE);

		if(monoSample) {
			colorBuffer = new GLTexture2D(_width, _height, PixelFormat.PXF_ARGB8);
			depthBuffer = new GLTexture2D(_width, _height, PixelFormat.PXF_D24S8);
		} else {
			colorBuffer = new GLBuffer(BufferNature.RenderBuffer);
			depthBuffer = new GLBuffer(BufferNature.RenderBuffer);

			GLBuffer.RenderBuffer.allocate(colorBuffer, _width, _height, PixelFormat.PXF_RGBA8, samples);
			GLBuffer.RenderBuffer.allocate(depthBuffer, _width, _height, PixelFormat.PXF_D24S8, samples);
		}

		GLBuffer.FBO.drawTo(renderFBO, colorBuffer, GL_COLOR_ATTACHMENT0);
		GLBuffer.FBO.drawTo(renderFBO, depthBuffer, GL_DEPTH_ATTACHMENT);

		gl.glViewport(0, 0, _width, _height);

		if(1 < samples) {
			if(msaaResolveFBO == null)
				msaaResolveFBO = new GLBuffer(BufferNature.FrameBuffer);

			if(readbackType == ReadbackType.READ_PIXELS) {
				msaaResolveBuffer = new GLBuffer(BufferNature.RenderBuffer);
				GLBuffer.RenderBuffer.allocate(msaaResolveBuffer, _width, _height, PixelFormat.PXF_RGBA8, 1);
			} else
				msaaResolveBuffer = new GLTexture2D(_width, _height, PixelFormat.PXF_RGBA8);

			GLBuffer.FBO.drawTo(msaaResolveFBO, msaaResolveBuffer, GL_COLOR_ATTACHMENT0);
		} else if(msaaResolveFBO != null) {
			msaaResolveBuffer.destroy();
			msaaResolveFBO.destroy();
		}

		// Setup read-back buffers
		resizeBuffers(_height, stride);
	}
	protected void 			resizeBuffers(final int height, final int stride) {
		super.reallocPBOs(BufferNature.PixelBufferPack, height, stride, GLBuffer.Mode.READ);
	}

	public void 			destroy() {
		destroyObjects();

		if(msaaResolveFBO != null)
			msaaResolveFBO.destroy();

		renderFBO.destroy();
	}
	protected void 			destroyObjects() {
		for (int i = 0; i < semaphores.length; i++) {
			if (processingState.get(i)) {
				GLRenderer.glBind(pbos[i], false);
				waitForProcessingToComplete(i);
			}
		}

		GLRenderer.glUnbind(BufferNature.PixelBufferPack, false);

		for (int i = 0; i < pbos.length; i++)
			if (pbos[i] != null)
				pbos[i].destroy();

		if (msaaResolveBuffer != null)
			msaaResolveBuffer.destroy();

		if (depthBuffer != null)
			depthBuffer.destroy();

		if (colorBuffer != null)
			colorBuffer.destroy();
	}

	protected void 			readBack(final GLBuffer _pbo) {
		GLRenderer.glBind(_pbo, false);

		// Stride in pixels
		gl.glPixelStorei(GL_PACK_ROW_LENGTH, stride >> 2);

		// Asynchronously transfer current frame
		if (readbackType == ReadbackType.READ_PIXELS) {
			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, msaaResolveFBO == null ? renderFBO.getId() : msaaResolveFBO.getId());
			gl.glReadPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 0);
			gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		} else {
			gl.glBindTexture(GL_TEXTURE_2D, msaaResolveFBO == null ? colorBuffer.getId() : msaaResolveBuffer.getId());
			gl.glGetTexImage(GL_TEXTURE_2D, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 0);
			gl.glBindTexture(GL_TEXTURE_2D, 0);
		}

		// Restore PACK_ROW_LENGTH
		gl.glPixelStorei(GL_PACK_ROW_LENGTH, 0);
	}

}