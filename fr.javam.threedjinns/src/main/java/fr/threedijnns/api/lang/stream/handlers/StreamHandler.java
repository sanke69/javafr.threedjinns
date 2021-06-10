package fr.threedijnns.api.lang.stream.handlers;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

public interface StreamHandler {
    void process(final int size, ByteBuffer data, Semaphore signal);
}