package fr.java.media.meshes;

import fr.java.nio.buffer.Point2DBufferX;
import fr.java.nio.buffer.Point3DBufferX;

public interface MeshObject {

	public Point3DBufferX getVertexBuffer();
	public Point3DBufferX getNormalBuffer();
	public Point3DBufferX getColorBuffer();

	public Point2DBufferX getTextureBuffer();

}
