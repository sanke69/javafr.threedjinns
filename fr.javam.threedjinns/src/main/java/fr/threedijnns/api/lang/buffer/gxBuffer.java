package fr.threedijnns.api.lang.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import fr.java.beans.reflect.utils.Primitives;
import fr.threedijnns.api.lang.enums.LockFlag;

public class gxBuffer {

	protected BufferBase 	m_Buffer;

	public gxBuffer(BufferBase _buffer) {
		m_Buffer = _buffer;
	}

	public Primitives getType() {
		if(m_Buffer != null)
            return m_Buffer.getType();
        return Primitives.UNDEF;
	}
	public long getCount() {
		if(m_Buffer != null)
            return m_Buffer.getLength();
        return 0;
	}
	public BufferBase getBuffer() {
		return m_Buffer;
	}
	
	ByteBuffer lock(long _offset, long _size, LockFlag _flags) {
        if(m_Buffer == null)
            return null;

        return m_Buffer.lock(_offset * getType().nbBytes(), _size * getType().nbBytes(), _flags);
	}
	void unlock() {
        if(m_Buffer == null)
            return ;

        m_Buffer.unlock();
	}

	public void fill(Buffer _data, long _count) {
		assert(_data != null);

//		ByteBuffer data = lock(0, m_Buffer.getLength(), LockFlag.WriteOnly);
		ByteBuffer data = lock(0, _count, LockFlag.WriteOnly);

		_data.limit((int) _count);

		switch(m_Buffer.getType()) {
		case BYTE:		data                  .put((ByteBuffer)   _data); break;
		case SHORT:		data.asShortBuffer()  .put((ShortBuffer)  _data); break;
		case INTEGER:	data.asIntBuffer()    .put((IntBuffer)    _data); break;
		case LONG:		data.asLongBuffer()   .put((LongBuffer)   _data); break;
		case FLOAT:		data.asFloatBuffer()  .put((FloatBuffer)  _data); break;
		case DOUBLE:	data.asDoubleBuffer() .put((DoubleBuffer) _data); break;
		case UNDEF: 	throw new IllegalArgumentException();
		}

        unlock();
	}
	void release() {
		m_Buffer = null;
	}


}
