package fr.threedijnns.api.lang.stream;

import fr.java.math.geometry.space.Frame3D;

// Used for GPU operation
public interface VertexBufferStream {

	public VertexBufferStream transform(Frame3D _locate);

}
