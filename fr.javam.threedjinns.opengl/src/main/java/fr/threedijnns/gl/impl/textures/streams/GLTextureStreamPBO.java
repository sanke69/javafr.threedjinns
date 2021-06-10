package fr.threedijnns.gl.impl.textures.streams;

import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.GLBuffer;
import fr.threedijnns.gl.impl.GLContextCapabilities;
import fr.threedijnns.gl.impl.GLStream;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public class GLTextureStreamPBO extends AbstractTextureStreamPBO {

    public GLTextureStreamPBO(final GLTexture2D _texture, final StreamHandler2D _handler, final int _transfersToBuffer) {
        super(_texture, _handler, _transfersToBuffer);
        
        
        
        System.out.println(this.getClass().getSimpleName() + ": created");
    }

    public void pinBuffer(final int index) {
    	pinnedBuffers[index] = pbos[index].lock(0, pbos[index].getLength(), LockFlag.WriteOnly);
    }

    protected void postProcess(final int index) {
        pbos[index].unlock();
    }

    protected void postUpload(final int index) {
    }

    public void destroy() {
        destroyObjects();
    }



    public static final GLStream.TextureFactory FACTORY = new GLStream.TextureFactory("Asynchronous PBO") {
		@Override
		public boolean isSupported() {
			GLContextCapabilities caps = GLContextCapabilities.get();
            return caps.OpenGL21 || caps.GL_ARB_pixel_buffer_object || caps.GL_EXT_pixel_buffer_object;
		}

        public TextureStream create(final GLTexture2D _texture, final StreamHandler2D handler, final int transfersToBuffer) {
            return new GLTextureStreamPBO(_texture, handler, transfersToBuffer);
        }

    };

}