/*
 * Copyright (c) 2002-2012 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fr.threedijnns.gl.rendering;

import static com.jogamp.opengl.GL.GL_BGRA;
import static com.jogamp.opengl.GL.GL_COLOR_ATTACHMENT0;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_ATTACHMENT;
import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_MAP_READ_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_RENDERBUFFER;
import static com.jogamp.opengl.GL.GL_RGBA8;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL2.GL_LAYOUT_LINEAR_CPU_CACHED_INTEL;
import static com.jogamp.opengl.GL2.GL_TEXTURE_MEMORY_LAYOUT_INTEL;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_8_8_8_8_REV;
import static fr.threedijnns.gl.JOGL.gl;

import java.nio.IntBuffer;

import fr.java.lang.enums.PixelFormat;
import fr.java.utils.primitives.Buffers;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.stream.StreamBuffered2D;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.GLStream;

/**
 * Optimized StreamPBOReader for Intel IGPs:
 * <p>
 * - We render to a standard FBO.
 * - We asynchronously blit to another FBO with INTEL_map_texture attachments (linear layout).
 * - We synchronously map the linear textures.
 * <p>
 */
public final class GLRenderStreamINTEL extends StreamBuffered2D implements IFrameStream {

    private final IntBuffer strideBuffer;
    private final IntBuffer layoutBuffer;

    private final int renderFBO;

//    private int rgbaBuffer;
//    private int depthBuffer;

    private final int resolveFBO;
    private final int[] resolveBuffers;

    private int samples;

    private int synchronousFrames;

	private GLBuffer rgbaBufferAlt;
	private GLBuffer depthBufferAlt;

    public GLRenderStreamINTEL(final StreamHandler2D handler, final int samples, final int transfersToBuffer) {
        super(handler, transfersToBuffer);

        final GLContextCapabilities caps = GLContextCapabilities.get();

        this.strideBuffer = Buffers.allocateIntBuffer(1);
        this.layoutBuffer = Buffers.allocateIntBuffer(1);

        renderFBO = GLRenderer.glGenBuffer(BufferNature.FrameBuffer);

        resolveFBO = GLRenderer.glGenBuffer(BufferNature.FrameBuffer);
        resolveBuffers = new int[transfersToBuffer];

        this.samples = GLRenderer.checkSampleCapability(samples, caps);
        System.out.println(this.getClass().getSimpleName() + ": created, msaa " + this.samples);
    }

    public StreamHandler2D getHandler() {
        return handler;
    }

    private void resize(final int width, final int height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("Invalid dimensions: " + width + " x " + height);

        destroyObjects();

        this.width = width;
        this.height = height;

        this.stride = GLRenderer.getStride(width);

        if (width == 0 || height == 0)
            return;

        bufferIndex = synchronousFrames = transfersToBuffer - 1;

        // Setup render FBO

        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, renderFBO);

        rgbaBufferAlt = new GLBuffer(BufferNature.RenderBuffer);
//		((GLRenderBuffer) rgbaBufferAlt).allocate(width, height, PixelFormat.PXF_RGBA8, samples);
		GLBuffer.RenderBuffer.allocate(rgbaBufferAlt, width, height, PixelFormat.PXF_RGBA8, samples);
//        rgbaBuffer = StreamUtil.createRenderBuffer(width, height, samples, GL_RGBA8);
        gl.glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, rgbaBufferAlt.getId());

        depthBufferAlt = new GLBuffer(BufferNature.RenderBuffer);
//		((GLRenderBuffer) depthBufferAlt).allocate(width, height, PixelFormat.PXF_D24S8, samples);
		GLBuffer.RenderBuffer.allocate(depthBufferAlt, width, height, PixelFormat.PXF_D24S8, samples);
//        depthBuffer = StreamUtil.createRenderBuffer(width, height, samples, GL_DEPTH24_STENCIL8);
        gl.glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBufferAlt.getId());

        gl.glViewport(0, 0, width, height);

        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

        for (int i = 0; i < resolveBuffers.length; i++)
            resolveBuffers[i] = genLayoutLinearTexture(width, height);

        gl.glBindTexture(GL_TEXTURE_2D, 0);
    }

    private static int genLayoutLinearTexture(final int width, final int height) {
        final int texID = GLRenderer.glGenBuffer(BufferNature.Texture);

        gl.glBindTexture(GL_TEXTURE_2D, texID);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MEMORY_LAYOUT_INTEL, GL_LAYOUT_LINEAR_CPU_CACHED_INTEL);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, null);

        return texID;
    }

    public void bind() {
        if (this.width != handler.getWidth() || this.height != handler.getHeight())
            resize(handler.getWidth(), handler.getHeight());

        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, renderFBO);
    }

    private void prepareFramebuffer(final int trgTEX) {
        // Back-pressure. Make sure we never buffer more than <transfersToBuffer> frames ahead.
        if (processingState.get(trgTEX))
            waitForProcessingToComplete(trgTEX);

        gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, renderFBO);
        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, resolveFBO);

        // Blit current texture
        gl.glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, resolveBuffers[trgTEX], 0);
        gl.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);

        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
    }

    public void swapBuffers() {
        if (width == 0 || height == 0)
            return;

        final int trgTEX = (int) (bufferIndex % transfersToBuffer);
        final int srcTEX = (int) ((bufferIndex - 1) % transfersToBuffer);

        prepareFramebuffer(trgTEX);

        // This will be non-zero for the first (transfersToBuffer - 1) frames
        // after start-up or a resize.
        if (0 < synchronousFrames) {
            // The srcTEX is currently empty. Wait for trgPBO's ReadPixels to complete and copy the current frame to srcTEX.
            // We do this to avoid sending an empty buffer for processing, which would cause a visible flicker on resize.
            copyFrames(trgTEX, srcTEX);
            synchronousFrames--;
        }

        // Time to process the srcTEX

        pinBuffer(srcTEX);

        // Send the buffer for processing

        processingState.set(srcTEX, true);
        semaphores[srcTEX].acquireUninterruptibly();

        handler.process(
                width, height,
                pinnedBuffers[srcTEX],
                stride,
                semaphores[srcTEX]
        );

        bufferIndex++;
    }

    private void copyFrames(final int src, final int trg) {
        // pinnedBuffers[src] = gl.glMapTexture2DINTEL(resolveBuffers[src], 0, height * stride, GL_MAP_READ_BIT, strideBuffer, layoutBuffer, pinnedBuffers[src]);
        pinnedBuffers[src] = gl.glMapTexture2DINTEL(resolveBuffers[src], 0, GL_MAP_READ_BIT, strideBuffer, layoutBuffer);
        // pinnedBuffers[trg] = gl.glMapTexture2DINTEL(resolveBuffers[trg], 0, height * stride, GL_MAP_WRITE_BIT, strideBuffer, layoutBuffer, pinnedBuffers[trg]);
        pinnedBuffers[trg] = gl.glMapTexture2DINTEL(resolveBuffers[trg], 0, GL_MAP_WRITE_BIT, strideBuffer, layoutBuffer);

        pinnedBuffers[trg].put(pinnedBuffers[src]);

        pinnedBuffers[src].flip();
        pinnedBuffers[trg].flip();

        gl.glUnmapTexture2DINTEL(resolveBuffers[trg], 0);
        gl.glUnmapTexture2DINTEL(resolveBuffers[src], 0);
    }

    private void pinBuffer(final int index) {
        final int texID = resolveBuffers[index];

        // pinnedBuffers[index] = gl.glMapTexture2DINTEL(texID, 0, height * stride, GL_MAP_READ_BIT, strideBuffer, layoutBuffer, pinnedBuffers[index]);
        pinnedBuffers[index] = gl.glMapTexture2DINTEL(texID, 0, GL_MAP_READ_BIT, strideBuffer, layoutBuffer);
        // System.out.println(String.format("size should be %d, actual %d", height * stride, pinnedBuffers[index].capacity()));
        // TODO: Row alignment is currently hardcoded to 16 pixels
        // We wouldn't need to do that if we could create a ByteBuffer
        // from an arbitrary address + length. Consider for LWJGL 3.0?
        checkStride(index, texID);
    }
    protected void postProcess(int index) {
        gl.glUnmapTexture2DINTEL(resolveBuffers[index], 0);
    }


    private void checkStride(final int index, final int texID) {
        if (strideBuffer.get(0) != stride) {
            System.err.println("Wrong stride: " + stride + ". Should be: " + strideBuffer.get(0));
            gl.glUnmapTexture2DINTEL(texID, 0);
            stride = strideBuffer.get(0);
            // pinnedBuffers[index] = gl.glMapTexture2DINTEL(texID, 0, height * stride, GL_MAP_READ_BIT, strideBuffer, layoutBuffer, pinnedBuffers[index]);
            pinnedBuffers[index] = gl.glMapTexture2DINTEL(texID, 0, GL_MAP_READ_BIT, strideBuffer, layoutBuffer);
        }
    }

    private void destroyObjects() {
        for (int i = 0; i < semaphores.length; i++) {
            if (processingState.get(i))
                waitForProcessingToComplete(i);
        }

        if (rgbaBufferAlt != null) rgbaBufferAlt.destroy(); //GLRenderer.glDeleteRenderbuffers(rgbaBuffer);
        if (depthBufferAlt != null) depthBufferAlt.destroy(); //GLRenderer.glDeleteRenderbuffers(depthBuffer);

        for (int i = 0; i < resolveBuffers.length; i++) {
            GLRenderer.glDeleteBuffer(BufferNature.Texture, resolveBuffers[i]);
//        	GLRenderer.glDeleteTextures(resolveBuffers[i]);
            resolveBuffers[i] = 0;
        }
    }

    public void destroy() {
        destroyObjects();

        if (resolveFBO != 0)
            GLRenderer.glDeleteBuffer(BufferNature.FrameBuffer, resolveFBO);
//        	GLRenderer.glDeleteFramebuffers(resolveFBO);
        GLRenderer.glDeleteBuffer(BufferNature.FrameBuffer, renderFBO);
//        GLRenderer.glDeleteFramebuffers(renderFBO);
    }


    
	public static final GLStream.RenderFactory FACTORY = new GLStream.RenderFactory("INTEL_map_texture") {

		public boolean isSupported() {
			final GLContextCapabilities caps = GLContextCapabilities.get();
            return caps.GL_INTEL_map_texture && (caps.OpenGL30 || caps.GL_ARB_framebuffer_object || caps.GL_EXT_framebuffer_blit);
		}

		public IFrameStream create(final StreamHandler2D handler, final int samples, final int transfersToBuffer) {
            return new GLRenderStreamINTEL(handler, samples, transfersToBuffer);
		}

	};

}