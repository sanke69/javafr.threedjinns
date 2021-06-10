package fr.threedijnns.gl.impl;

import static com.jogamp.opengl.GL2ES3.GL_TIMEOUT_IGNORED;
import static fr.threedijnns.gl.JOGL.gl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;
import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.gl.impl.textures.GLTexture2D;

public interface GLStream {

	// From OpenGL to Outer World
	public abstract class RenderFactory {
		String name;

		protected RenderFactory(String _name) {
			super();
			name = _name;
		}
		
		public abstract boolean 		isSupported();
        public abstract IFrameStream 	create(final StreamHandler2D handler, final int samples, final int transfersToBuffer);
	}

	// From Outer World to OpenGL
	public abstract class TextureFactory {
		String name;

		protected TextureFactory(String _name) {
			super();
			name = _name;
		}
		
		public abstract boolean 		isSupported();
        public abstract TextureStream 	create(final GLTexture2D _texture, final StreamHandler2D handler, final int transfersToBuffer);
	}

	public static class Fence {

	    public static void waitOnFence(final long[] fences, final int index) {
	        gl.glWaitSync(fences[index], 0, GL_TIMEOUT_IGNORED);
	        gl.glDeleteSync(fences[index]);
	        fences[index] = 0;
	    }

	}

	public static final class PageSizeProvider {

    	public static final int PAGE_SIZE;

        static {
            int pageSize = 4096; // Assume 4kb if Unsafe is not available

            try {
                pageSize = getUnsafeInstance().pageSize();
            } catch (Exception e) {
                // ignore
            }

            PAGE_SIZE = pageSize;
        }

        private static Unsafe getUnsafeInstance() {
            final Field[] fields = Unsafe.class.getDeclaredFields();

			/*
            Different runtimes use different names for the Unsafe singleton,
			so we cannot use .getDeclaredField and we scan instead. For example:

			Oracle: theUnsafe
			PERC : m_unsafe_instance
			Android: THE_ONE
			*/
            for (Field field : fields) {
                if (!field.getType().equals(Unsafe.class))
                    continue;

                final int modifiers = field.getModifiers();
                if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)))
                    continue;

                field.setAccessible(true);
                try {
                    return (Unsafe) field.get(null);
                } catch (IllegalAccessException e) {
                    // ignore
                }
                break;
            }

            throw new UnsupportedOperationException();
        }
    }

}
