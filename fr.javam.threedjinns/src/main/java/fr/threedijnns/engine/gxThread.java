package fr.threedijnns.engine;

public interface gxThread {

	public boolean 	isRunning();
	public boolean 	isGxThread();

	public void 	start(Runnable _init, Runnable _mainLoop, Runnable _finalize);
	public void 	stop();

	public void    	runLater(Runnable _instructions);

}
