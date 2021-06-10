package fr.threedijnns.objects.space.shapes.meshes;

import fr.java.math.geometry.space.Frame3D;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;

public class GxMeshObjectVBO implements GxRenderable {
	protected int	m_VertexBuffer;
	protected long	m_VertexCount;

	protected int	m_IndiceBuffer;
	protected long	m_IndiceCount;

	protected boolean hasNormal;
	protected int		m_NormalBuffer;
	protected long	m_NormalCount;

	protected boolean hasColor;
	protected int 	m_ColorBuffer;
	protected long	m_ColorCount;

	protected boolean hasText0;
	protected int 	m_Text0Buffer;
	protected long	m_Text0Count;

	protected Frame3D m_MeshTransformation;
	public GxMeshObjectVBO() {
		m_VertexBuffer = 0;
		m_VertexCount = 0;
		m_IndiceBuffer = 0;
		m_IndiceCount = 0;
	}
	public GxMeshObjectVBO(GxMeshObject _obj) {
		super();
		initialize(_obj);
	}

	public void process() {}
	public void render() {
		/*
		if(m_VertexBuffer == 0 || m_IndiceBuffer == 0)
			return ;

		gl.glEnableClientState	(GL2.GL_VERTEX_ARRAY);
		gl.glBindBuffer			(GL2.GL_ARRAY_BUFFER, m_VertexBuffer);
		gl.glVertexPointer		(3, GL2.GL_FLOAT, 0, 0);

		if(hasNormal) {
			gl.glEnableClientState	(GL2.GL_NORMAL_ARRAY);
			gl.glBindBuffer			(GL2.GL_ARRAY_BUFFER, m_NormalBuffer);
			gl.glNormalPointer		(GL2.GL_FLOAT, 0, 0);
		}
		if(hasColor) {
			gl.glEnableClientState	(GL2.GL_COLOR_ARRAY);
			gl.glBindBuffer			(GL2.GL_ARRAY_BUFFER, m_ColorBuffer);
			gl.glColorPointer		(4, GL.GL_UNSIGNED_BYTE, 0, 0);
		}
		if(hasText0) {
			gl.glClientActiveTexture(GL.GL_TEXTURE0);
			gl.glBindBuffer			(GL2.GL_ARRAY_BUFFER, m_Text0Buffer);
			gl.glTexCoordPointer	(2, GL2.GL_FLOAT, 0, 0);
		}

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, m_IndiceBuffer);
		gl.glDrawElements(GL.GL_TRIANGLES, (int) (m_IndiceCount), GL.GL_UNSIGNED_INT, 0);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

		if(hasText0)
			gl.glDisableClientState(GL.GL_TEXTURE0);
		if(hasNormal)
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		if(hasNormal)
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
*/
	}

	public void initialize(GxMeshObject _obj) {
		/*
		float[] vertices = null;
		int[][] indices = null;
		int[]   flatten_indices = null;
		int     newIndex = 0;

		switch(_obj.getVersion()) {
		case OBJ :		MeshOBJ obj = (MeshOBJ) _obj;
						vertices = new float[3 * obj.vertex().size()];
						for(int j = 0; j < obj.vertex().size(); ++j) {
							Vector3f p = new Vector3f(
													(float) obj.scale().x * obj.vertex(j).x,
													(float) obj.scale().y * obj.vertex(j).y,
													(float) obj.scale().z * obj.vertex(j).z);
							p = get_world_coordinate(p, obj.land(), false);

							vertices[3 * j + 0] = p.x;
							vertices[3 * j + 1] = p.y;
							vertices[3 * j + 2] = p.z;
						}
						indices = new int[obj.mesh().size()][];
						for(int k = 0; k < obj.mesh().size(); ++k) {
							indices[k] = new int[3 * obj.mesh(k).faces.size()];
							for(int j = 0; j < obj.mesh(k).faces.size(); ++j) {
								indices[k][j + 0] = obj.mesh(k).faces.get(j).v[0] + newIndex;
								indices[k][j + 1] = obj.mesh(k).faces.get(j).v[1] + newIndex;
								indices[k][j + 2] = obj.mesh(k).faces.get(j).v[2] + newIndex;
							}
						}
						break;
		case PLY :		CMeshObjectV2 objV2 = (CMeshObjectV2) _obj;
						vertices = new float[3 * objV2.vertex().size()];
						for(int j = 0; j < objV2.vertex().size(); ++j) {
							Vector3f p = new Vector3f(
													(float) objV2.scale().x * objV2.vertex(j).v.x,
													(float) objV2.scale().y * objV2.vertex(j).v.y,
													(float) objV2.scale().z * objV2.vertex(j).v.z);
							p = get_world_coordinate(p, objV2.land(), false);

							vertices[3 * j + 0] = p.x;
							vertices[3 * j + 1] = p.y;
							vertices[3 * j + 2] = p.z;
						}
						indices = new int[objV2.polygon().size()][];
						for(int k = 0; k < objV2.polygon().size(); ++k) {
							indices[k] = new int[3];
							indices[k][0] = objV2.polygon(k).id[0] + newIndex;
							indices[k][1] = objV2.polygon(k).id[1] + newIndex;
							indices[k][2] = objV2.polygon(k).id[2] + newIndex;
						}
						break;
		};

		flatten_indices = ArrayHelper.flatten(indices);

		m_VertexCount = vertices.length;
		m_IndiceCount = flatten_indices.length;

		if(m_VertexBuffer != 0) {
			gl.glDeleteBuffers(1, IntBuffer.wrap(new int[] { m_VertexBuffer }));
			m_VertexBuffer = 0;
		}
		if(m_IndiceBuffer != 0) {
			gl.glDeleteBuffers(1, IntBuffer.wrap(new int[] { m_IndiceBuffer }));
			m_IndiceBuffer = 0;
		}

		if(m_VertexCount != 0 && m_IndiceCount != 0) {
			IntBuffer tmp = IntBuffer.wrap(new int[] { 0 });
			gl.glGenBuffers(1, tmp); m_VertexBuffer = tmp.get(0);
			gl.glGenBuffers(1, tmp); m_IndiceBuffer = tmp.get(0);

			FloatBuffer  vertexes = FloatBuffer.wrap(vertices);
			IntBuffer    indexes  = IntBuffer.wrap(flatten_indices);
			
			ByteBuffer bb = ByteBuffer.allocate(vertexes.capacity() * 8);
			for(int i = 0; i < vertexes.capacity(); ++i)
				bb.putDouble(vertexes.get());

			System.out.println(indexes.capacity());
			ByteBuffer bbi = ByteBuffer.allocate(indexes.capacity() * 4);
			for(int i = 0; i < indexes.capacity(); ++i)
				bbi.putInt(indexes.get());
			vertexes.rewind();
			indexes.rewind();
			
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,         m_VertexBuffer);
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, m_IndiceBuffer);

			gl.glBufferData(GL2.GL_ARRAY_BUFFER,		 m_VertexCount * Float.BYTES,	vertexes, GL2.GL_STATIC_DRAW);
			gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, m_IndiceCount * Integer.BYTES, indexes,  GL2.GL_STATIC_DRAW);

			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,         0);
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		*/
	}
	public void dispose() {
		//gl.glDeleteBuffers(1, IntBuffer.wrap(new int[] { m_VertexBuffer }));
		//gl.glDeleteBuffers(1, IntBuffer.wrap(new int[] { m_IndiceBuffer }));		
	}

}
