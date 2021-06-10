package fr.threedijnns.api.lang.buffer;

import java.nio.ByteBuffer;

import fr.java.beans.reflect.utils.Primitives;
import fr.threedijnns.api.lang.enums.LockFlag;

public abstract class AbstractBufferBase implements BufferBase {
	protected Primitives	primitive;
	protected long			capacity;
	protected ByteBuffer	buffer;

	protected AbstractBufferBase() {
		super();
		primitive = null;
		capacity  = -1L;
		buffer    = null;
	}
	protected AbstractBufferBase(Primitives _type, long _count) {
		super();
		primitive = _type;
		capacity  = _count;
		buffer    = null;
	}

	@Override
	public Primitives 	getType() { return primitive; }
	@Override
	public long 		getLength() { return capacity; }

	@Override
	public ByteBuffer lock(long _offset, long _size, LockFlag _flags) {
		if(buffer == null)
			return null;

		buffer.position((int) _offset);
		buffer.limit((int) _size);
		return buffer;
	}
	@Override
	public void       unlock() {
		if(buffer == null)
			return ;

		buffer.position(0);
		buffer.limit((int) capacity);
	}

}
