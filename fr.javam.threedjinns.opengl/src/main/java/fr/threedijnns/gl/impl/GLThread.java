package fr.threedijnns.gl.impl;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import fr.java.sdk.log.LogInstance;
import fr.java.sdk.time.tools.TimeWatchImpl;

import fr.threedijnns.engine.gxThread;

public class GLThread implements gxThread {
	public static final LogInstance log = LogInstance.getLogger("JOGL [3Djinn]");

	private static GLThread	instance;
	public static GLThread instance() {
		if(instance == null)
			instance = new GLThread();
		return instance;
	}
	
	private GLThread() {
		super();
		
		thread 			= null;
		pendingRequests	= new ConcurrentLinkedQueue<>();

		isStarted		= new CountDownLatch(1);
		isRunning		= false;
	}

	public static final int 				maxFps = 30;
	public static final TimeWatchImpl			timeFps = new TimeWatchImpl();

	private Thread    						thread;
	private ConcurrentLinkedQueue<Runnable> pendingRequests;

	private CountDownLatch 					isStarted;
	private boolean 						isRunning;

	@Override
	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}
	@Override
	public boolean isGxThread() {
		return Thread.currentThread() == thread;
	}

	@Override
	public void runLater(Runnable _glRequest) {
		if(Thread.currentThread() != thread)
			pendingRequests.add(_glRequest);
		else
			_glRequest.run();
	}

	@Override
	public void start(Runnable _init, Runnable _mainLoop, Runnable _finalize) {
		if(_mainLoop == null)
			throw new IllegalArgumentException("main loop can't be null !!!");

		thread = new Thread(() -> {
			isRunning = true;

			if(_init != null)
				_init.run();
			isStarted.countDown();

			while(isRunning) {
				timeFps.tic();

				treatRequests();

				_mainLoop.run();
				
				timeFps.toc();

				try {
					long delay = (long) ((1000 - timeFps.getInMilliseconds()) / maxFps);
					Thread.sleep(delay > 0 ? delay : 0);
				} catch (InterruptedException e) { }
			}

			System.err.println("OpenGL Thread stopped");
			
			if(_finalize != null)
				_finalize.run();
		}, "JOGL Thread") {{ isRunning = false; }};

		try { 
			thread.start();
			isStarted.await();
		} catch(InterruptedException e) { e.printStackTrace(); System.err.println("OpenGL Thread crashed"); }

		log.info("OpenGL started");
	}
	@Override
	public void stop() {
		isRunning = false;
	}
	
	private void treatRequests() {
		Runnable runnable;
		while((runnable = pendingRequests.poll()) != null)
			runnable.run();
	}

}
