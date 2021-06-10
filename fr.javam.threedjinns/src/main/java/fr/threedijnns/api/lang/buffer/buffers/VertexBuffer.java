package fr.threedijnns.api.lang.buffer.buffers;

import fr.threedijnns.api.lang.stream.VertexBufferStream;

public interface VertexBuffer {

	public VertexBufferStream stream(); // throw NotAvailableException

}
