package fr.threedijnns.api.lang.stream;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.Semaphore;

import fr.threedijnns.api.lang.stream.handlers.StreamHandler;

public abstract class StreamBuffered {
    protected final StreamHandler 	handler;

    protected final int 			transfersToBuffer; // 3 provides optimal concurrency in most cases

    protected final ByteBuffer[] 	pinnedBuffers;
    protected final Semaphore[]  	semaphores;

    protected final BitSet 			processingState;

    protected int 					size;
    protected long 					bufferIndex;

    protected StreamBuffered(final StreamHandler _handler, final int _transfersToBuffer) {
        handler           = _handler;
        transfersToBuffer = _transfersToBuffer;

        processingState   = new BitSet(transfersToBuffer);

        pinnedBuffers     = new ByteBuffer[transfersToBuffer];
        semaphores        = new Semaphore[transfersToBuffer];
        for(int i = 0; i < semaphores.length; i++)
            semaphores[i] = new Semaphore(1, false);

    }

    protected void waitForProcessingToComplete(final int index) {
        final Semaphore s = semaphores[index];
        // Early-out: start-up or handler has finished processing
        if(s.availablePermits() == 0) {
            // This will block until handler has finished processing
            s.acquireUninterruptibly();
            // Give the permit back
            s.release();
        }

        postProcess(index);
        processingState.set(index, false);
    }

    protected abstract void postProcess(int index);

}