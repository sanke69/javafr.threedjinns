package fr.threedijnns.api.lang.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import fr.java.beans.reflect.utils.Primitives;
import fr.threedijnns.api.lang.enums.LockFlag;

public interface BufferBase {

	public Primitives	 	getType();
    public long   			getLength();

	public ByteBuffer 		lock(long _offset, long _size, LockFlag _flags);
	public void   			unlock();
	public void   			update(long _offset, long _size, LockFlag _flags, Buffer _data);

    public Buffer 			getCopy();

}