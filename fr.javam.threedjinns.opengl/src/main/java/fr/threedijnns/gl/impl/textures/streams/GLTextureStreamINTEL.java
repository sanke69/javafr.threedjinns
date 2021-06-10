package fr.threedijnns.gl.impl.textures.streams;

import static com.jogamp.opengl.GL.GL_BGRA;
import static com.jogamp.opengl.GL.GL_COLOR_ATTACHMENT0;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_RGBA8;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL2.GL_LAYOUT_LINEAR_CPU_CACHED_INTEL;
import static com.jogamp.opengl.GL2.GL_TEXTURE_MEMORY_LAYOUT_INTEL;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_8_8_8_8_REV;
import static fr.threedijnns.gl.JOGL.gl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import fr.java.lang.enums.PixelFormat;
import fr.java.utils.primitives.Buffers;
import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.stream.StreamBuffered2D;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.GLStream;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public final class GLTextureStreamINTEL extends StreamBuffered2D implements TextureStream {

    private final IntBuffer strideBuffer;
    private final IntBuffer layoutBuffer;

    private final int texFBO;
    private final int bufferFBO;

    GLTexture2D texture;

    private int[] buffers;

    private long currentIndex;

    private boolean resetTexture;

    GLTextureStreamINTEL(final StreamHandler2D handler, final int transfersToBuffer) {
        super(handler, transfersToBuffer);

        this.strideBuffer = Buffers.allocateIntBuffer(1);
        this.layoutBuffer = Buffers.allocateIntBuffer(1);

        texFBO = GLRenderer.glGenBuffer(BufferNature.FrameBuffer);
        bufferFBO = GLRenderer.glGenBuffer(BufferNature.FrameBuffer);

        buffers = new int[transfersToBuffer];
        System.out.println(this.getClass().getSimpleName() + ": created");
    }

    public StreamHandler2D getHandler() {
        return handler;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

        bufferIndex = 0;
        currentIndex = 0;

        resetTexture = true;

        texture = new GLTexture2D(width, height, PixelFormat.PXF_ARGB8);
        texture.setFilters(GL_LINEAR, GL_LINEAR);
 //       texID = StreamUtil.createRenderTexture(width, height, GL_LINEAR);

        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, texFBO);
        gl.glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId()/*texID*/, 0);
        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

        for (int i = 0; i < buffers.length; i++)
            buffers[i] = genLayoutLinearTexture(width, height);

        gl.glBindTexture(GL_TEXTURE_2D, 0);
    }

    private static int genLayoutLinearTexture(final int width, final int height) {
        final int texID = GLRenderer.glGenBuffer(BufferNature.Texture);

        gl.glBindTexture(GL_TEXTURE_2D, texID);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MEMORY_LAYOUT_INTEL, GL_LAYOUT_LINEAR_CPU_CACHED_INTEL);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) null);

        return texID;
    }

    public void snapshot() {
        if (width != handler.getWidth() || height != handler.getHeight())
            resize(handler.getWidth(), handler.getHeight());

        if (width == 0 || height == 0)
            return;

        final int trgPBO = (int) (bufferIndex % transfersToBuffer);

        // Back-pressure. Make sure we never buffer more than <transfersToBuffer> frames ahead.

        if (processingState.get(trgPBO))
            syncCopy(trgPBO);

        // pinnedBuffers[trgPBO] = gl.glMapTexture2DINTEL(buffers[trgPBO], 0, height * stride, GL_MAP_WRITE_BIT, strideBuffer, layoutBuffer, pinnedBuffers[trgPBO]);
        pinnedBuffers[trgPBO] = gl.glMapTexture2DINTEL(buffers[trgPBO], 0, GL_MAP_WRITE_BIT, strideBuffer, layoutBuffer);

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

        if (resetTexture) {
            syncCopy(trgPBO);
            resetTexture = true;
        }
    }

    public void tick() {
        final int srcPBO = (int) (currentIndex % transfersToBuffer);
        if (!processingState.get(srcPBO))
            return;

        // Try again next frame
        if (!semaphores[srcPBO].tryAcquire())
            return;

        semaphores[srcPBO].release(); // Give it back

        postProcess(srcPBO);
        processingState.set(srcPBO, false);

        copyTexture(srcPBO);
    }

    protected void postProcess(final int index) {
        gl.glUnmapTexture2DINTEL(buffers[index], 0);
    }

    private void syncCopy(final int index) {
        waitForProcessingToComplete(index);
        copyTexture(index);
    }

    private void copyTexture(final int index) {
        gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, bufferFBO);
        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, texFBO);

        gl.glFramebufferTexture2D(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, buffers[index], 0);
        gl.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        gl.glFramebufferTexture2D(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, 0, 0);

        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

        currentIndex++;
    }

    private void destroyObjects() {
        for (int i = 0; i < semaphores.length; i++) {
            if (processingState.get(i))
                waitForProcessingToComplete(i);
        }

        for (int i = 0; i < buffers.length; i++) {
        	GLRenderer.glDeleteBuffer(BufferNature.Texture, buffers[i]);
//        	GLRenderer.glDeleteTextures(buffers[i]);
            buffers[i] = 0;
        }

    	texture.destroy();
//        GLRenderer.glDeleteTextures(texID);
    }

    public void destroy() {
        destroyObjects();

        GLRenderer.glDeleteBuffer(BufferNature.FrameBuffer, bufferFBO);
        GLRenderer.glDeleteBuffer(BufferNature.FrameBuffer, texFBO);
//        GLRenderer.glDeleteFramebuffers(bufferFBO);
 //       GLRenderer.glDeleteFramebuffers(texFBO);
    }

    public static final GLStream.TextureFactory FACTORY = new GLStream.TextureFactory("INTEL_map_texture") {
		@Override
		public boolean isSupported() {
			GLContextCapabilities caps = GLContextCapabilities.get();
            return caps.GL_INTEL_map_texture && (caps.OpenGL30 || caps.GL_ARB_framebuffer_object || caps.GL_EXT_framebuffer_blit);
		}

        public TextureStream create(final GLTexture2D _texture, final StreamHandler2D handler, final int transfersToBuffer) {
            return new GLTextureStreamINTEL(handler, transfersToBuffer);
        }

    };

}