package fr.threedijnns.gl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jogamp.nativewindow.AbstractGraphicsDevice;
import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;

import jogamp.opengl.GLDrawableFactoryImpl;

import fr.java.sdk.log.LogInstance;
import fr.threedijnns.api.graphics.GxViewport;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.engine.gxBufferManager;
import fr.threedijnns.engine.gxDrawer;
import fr.threedijnns.engine.gxDrawerObjects;
import fr.threedijnns.engine.gxDrawerOption;
import fr.threedijnns.engine.gxDrawerPrimitives;
import fr.threedijnns.engine.gxEngine;
import fr.threedijnns.engine.gxInitializer;
import fr.threedijnns.engine.gxThread;
import fr.threedijnns.gl.impl.GLRenderer;
import fr.threedijnns.gl.impl.GLThread;
import fr.threedijnns.gl.rendering.AbstractRenderStreamPBO.ReadbackType;
import fr.threedijnns.gl.rendering.GLRenderStreamPBOCopy;

public class JOGL implements gxInitializer {
	public static final LogInstance log = LogInstance.getLogger("JOGL [3Djinn]");

    public static final String GLES1  = GLProfile.GLES1;
    public static final String GLES2  = GLProfile.GLES2;
    public static final String GLES3  = GLProfile.GLES3;
    public static final String GL2    = GLProfile.GL2;
    public static final String GL2ES1 = GLProfile.GL2ES1;
    public static final String GL2ES2 = GLProfile.GL2ES2;
    public static final String GL2GL3 = GLProfile.GL2GL3;
    public static final String GL3    = GLProfile.GL3;
    public static final String GL3bc  = GLProfile.GL3bc;
    public static final String GL4    = GLProfile.GL4;
	public static final String GL4bc  = GLProfile.GL4bc;
    public static final String GL4ES3 = GLProfile.GL4ES3;

	// Pointer on GL Native Library functions
	private GLOffscreenAutoDrawable pbuffer = null;
	private GLProfile 				profile = null;
    private GLContext 				context = null;
	public  static GL4bc            gl;

	// Reference on implementation
	private GLRenderer 				renderer;

	// List of active GL Viewport
	private List<GxViewport> 		viewports;

	private static JOGL instance;
	public static  JOGL instance() {	
		if(instance == null)
			instance = new JOGL(GLProfile.GL2ES1);
		return instance;
	};
	public static  JOGL instance(String _profile) {	
		if(instance == null)
			instance = new JOGL(_profile);
		return instance;
	};

//	public static void addViewportBuffer(IGLViewport _glRenderBuffer) {
//		instance().viewports.add(_glRenderBuffer);
//	}

	private JOGL(String _profile) {
		super();
		if(profile != null) {
			if(profile.getName().compareTo(_profile) != 0)
				throw new IllegalArgumentException("Can't request another profile!");
		} else
			profile = GLProfile.get(_profile);

//		viewports = new ArrayList<IGLViewport>();
		viewports = new CopyOnWriteArrayList<GxViewport>();
	}

	@Override public gxThread 			getThread() 					{ return GLThread.instance(); }

	@Override public gxEngine 			getEngineInstance() 			{ return renderer; }
	@Override public gxBufferManager 	getBufferManagerInstance() 		{ return renderer; }
	@Override public gxDrawer 			getDrawerInstance() 			{ return renderer; }
	@Override public gxDrawerOption 	getDrawerOptionInstance() 		{ return renderer; }
	@Override public gxDrawerPrimitives getDrawerPrimitiveInstance() 	{ return renderer; }
	@Override public gxDrawerObjects 	getDrawerObjectInstance() 		{ return renderer; }

	@Override
	public void startEngine() {
		if(profile == null)
			profile = GLProfile.get(GLProfile.GL2ES1);

		GLThread.instance().start(
				this::openGL_init, 
				this::openGL_mainLoop, 
				this::openGL_finalize);
	}
	@Override
	public void stopEngine() {
		GLThread.instance().stop();
	}

	public IFrameStream	registerViewport(GxViewport _viewport) {
		IFrameStream stream = 
//NOK							new RenderStreamINTEL		(_viewport.getRenderingStream(), 4, 3);
//NOK							new RenderStreamPBOAMD		(_viewport.getRenderingStream(), 4, 3);
//								new RenderStreamPBODefault	(_viewport.getRenderingStream(), 4, 3, ReadbackType.READ_PIXELS);
//OK							new GLRenderStreamPBO		(_viewport.getRenderingStream(), 4, 3, ReadbackType.READ_PIXELS);
								new GLRenderStreamPBOCopy	(_viewport.getRenderingStream(), 4, 3, ReadbackType.READ_PIXELS);
		
		viewports.add(_viewport);
		return stream;
	}

	private void openGL_init() {
		GLCapabilities caps = new GLCapabilities(profile);
		caps.setPBuffer(true);						// REQUIRED
		caps.setHardwareAccelerated(true);			// OPTIONAL
		caps.setDepthBits(24);						// OPTIONAL
//		caps.setOnscreen(false);					// OPTIONAL

		AbstractGraphicsDevice  device = null;

		GLProfile               profile = caps.getGLProfile();
		GLDrawableFactoryImpl   factory = GLDrawableFactoryImpl.getFactoryImpl(profile);
		pbuffer = factory.createOffscreenAutoDrawable(device, caps, null, 1, 1);
		pbuffer.setRealized(true);

		context = pbuffer.createContext(context);
		context.makeCurrent();

		gl = (GL4bc) context.getGL();

		renderer = new GLRenderer();
	}
	private void openGL_mainLoop() {
		for(GxViewport viewport : viewports) {
			viewport.getRenderStream().bind();

			if(viewport.getRenderer() != null) {
				viewport.getRenderer().process();
				viewport.getRenderer().render();
			}

			viewport.getRenderStream().swapBuffers();
		}
	}
	private void openGL_finalize() {
		for(GxViewport viewport : viewports)
			viewport.getRenderStream().destroy();
		pbuffer.destroy();
	}

}
