package fr.threedijnns.gl.impl.textures.streams;

import static com.jogamp.opengl.GL.GL_BGRA;
import static com.jogamp.opengl.GL.GL_RGBA;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2ES2.GL_UNPACK_ROW_LENGTH;
import static com.jogamp.opengl.GL2ES3.GL_PIXEL_UNPACK_BUFFER;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_8_8_8_8_REV;
import static fr.threedijnns.gl.JOGL.gl;

import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public abstract class AbstractTextureStreamPBO extends GLBuffer.StreamBufferedPBO implements TextureStream {
    private final GLTexture2D 	texture;

    private long 				currentIndex;
    private boolean 			resetTexture;

    protected AbstractTextureStreamPBO(GLTexture2D _texture, final StreamHandler2D handler, final int transfersToBuffer) {
        super(handler, transfersToBuffer);
        texture = _texture;
    }

    public StreamHandler2D 	getHandler() {
        return handler;
    }

    public int 				getWidth() {
        return width;
    }
    public int 				getHeight() {
        return height;
    }

    public void 			snapshot() {
        if (width != handler.getWidth() || height != handler.getHeight())
            resize(handler.getWidth(), handler.getHeight());

        if (width == 0 || height == 0)
            return;

        final int trgPBO = (int) (bufferIndex % transfersToBuffer);

        // Back-pressure. Make sure we never buffer more than <transfersToBuffer> frames ahead.

        if (processingState.get(trgPBO))
            syncUpload(trgPBO);

        pinBuffer(trgPBO);

        // Send the buffer for processing

        processingState.set(trgPBO, true);
        semaphores[trgPBO].acquireUninterruptibly();

        handler.process(width, height, pinnedBuffers[trgPBO], stride, semaphores[trgPBO]);

        bufferIndex++;

        if (resetTexture) // Synchronize to show the first frame immediately
            syncUpload(trgPBO);
    }
    public void 			tick() {
        final int srcPBO = (int) (currentIndex % transfersToBuffer);
        if (!processingState.get(srcPBO))
            return;

        syncUpload(srcPBO);
    }
    public void 			destroy() {
        destroyObjects();
    }

    protected abstract void pinBuffer(final int index);

    protected abstract void postUpload(int index);

    private void 			resize(final int width, final int height) {
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

        // Setup upload buffers

        reallocPBOs(BufferNature.PixelBufferUnpack, height, stride, GLBuffer.Mode.DRAW);
    }

    protected void 			destroyObjects() {
        for(int i = 0; i < semaphores.length; i++)
            if(processingState.get(i)) {
            	GLRenderer.glBind(pbos[i], false);
                waitForProcessingToComplete(i);
            }

        gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

        for(int i = 0; i < pbos.length; i++)
            if(pbos[i] != null)
            	pbos[i].destroy();
    }

    private void syncUpload(final int index) {
    	GLRenderer.glBind(pbos[index], false);

        waitForProcessingToComplete(index);

        upload(index);

    	GLRenderer.glUnbind(pbos[index].getNature(), false);
    }

    private void upload(final int srcPBO) {
//  	GLBuffer.PBO.copyFrom(pbos[srcPBO], texture, 0, 0, (int) pbos[srcPBO].getLength());

        // Asynchronously upload current update
        gl.glBindTexture(GL_TEXTURE_2D, texture.getId());
        gl.glPixelStorei(GL_UNPACK_ROW_LENGTH, stride >> 2);
        if (resetTexture) {
            gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 0);
            resetTexture = false;
        } else
            gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, 0);
        gl.glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        postUpload(srcPBO);
        currentIndex++;
    }

}
