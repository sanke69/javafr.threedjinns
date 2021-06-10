package fr.threedijnns.engine;

import fr.threedijnns.api.graphics.GxViewport;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;

public interface gxInitializer {

	public void 				startEngine();
	public void	 				stopEngine();

	public gxThread 			getThread();

	public gxEngine 			getEngineInstance();
	public gxBufferManager 		getBufferManagerInstance();

	public gxDrawer 			getDrawerInstance();
	public gxDrawerOption 		getDrawerOptionInstance();
	public gxDrawerPrimitives 	getDrawerPrimitiveInstance();
	public gxDrawerObjects 		getDrawerObjectInstance();

	public IFrameStream			registerViewport(GxViewport _viewport);	// Async method

}
