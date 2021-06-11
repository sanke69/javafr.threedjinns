package fr.threedijnns.objects.space.shapes.vbo;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.types.Points;
import fr.java.utils.Buffers3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public class GxCone3D extends AbstractVBObject {

    protected int m_Precision;

	public GxCone3D(float _w, float _h, float _d, int _p) {
		super(_w, _h, _d);
		m_Precision = _p;

        build(_p);
    }

	@Override
	public void process() {}
	@Override
	public void setDeclarationAndBuffers() {
		if(m_VertexBuffer == null || m_IndexBuffer == null)
			return ;

		gx.setDeclaration(m_Declaration);

		gx.setVertexBuffer(0, m_VertexBuffer);
		gx.setVertexBuffer(1, m_NormalBuffer);

		gx.setIndexBuffer(m_IndexBuffer);

		gx.drawIndexedPrimitives(m_PrimitiveType, 0, m_IndexBuffer.getCount() / 2);
		gx.drawIndexedPrimitives(m_PrimitiveType, m_IndexBuffer.getCount() / 2, m_IndexBuffer.getCount() / 2);
	}

	private void build(int _n) {
		float halfwidth  = (float) boundBox.getWidth() / 2.0f,
	          halfheight = (float) boundBox.getHeight() / 2.0f,
	          z0         = 0.0f,
	          z1         = (float) boundBox.getDepth(),
	          anglestep  = (float) (2.0f * Math.PI / (_n + 2)),
	          ca, sa;

		Point3D[]  vertex_tab = new Point3D[_n + 4];
		Vector3D[] normal_tab = new Vector3D[_n + 4];
        Point2D[]     map_tab = new Point2D[_n + 4];
        int[]       index_tab = new int[2 * (_n + 4)];
/*
        for(int i = 0; i < _n + 4; ++i) {
            vertex_tab[i] = new Vector3D.Double();
            normal_tab[i] = new Vector3D.Double();
            map_tab[i]    = new Vector2D.Double();
        }
*/
        for(int i = 0; i < _n + 2; i++) {
            ca = (float) Math.cos(i * anglestep);
            sa = (float) Math.sin(i * anglestep);

            vertex_tab[i] = Points.of(halfwidth  * (1 + ca), halfheight * (1 + sa), z0);
            normal_tab[i] = Vectors.of(ca, sa, 0.0f);
            map_tab[i]    = Points.of((1.0f + ca) / 2.0f, (1.0f + sa) / 2.0f);
        }

        vertex_tab[_n + 2] = Points.of(halfwidth, halfheight, z0);
        vertex_tab[_n + 3] = Points.of(halfwidth, halfheight, z1);
        normal_tab[_n + 2] = Vectors.of(0.0f, 0.0f, -1.0f);
        normal_tab[_n + 3] = Vectors.of(0.0f, 0.0f,  1.0f);

        index_tab[0] = _n + 2;
        for(int i = 0; i < _n + 2; i++)
            index_tab[i + 1] = i;
        index_tab[_n + 3] = 0;

        index_tab[_n + 4] = _n + 3;
        for(int i = 0; i < _n + 2; i++)
            index_tab[_n + 5 + i] = i;
        index_tab[2 * _n + 7] = 0;

//      setPolygonMode(PolygonMode.PM_SKELETON);

        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
    		new DeclarationElement(0, ElementUsage.Position,  ElementType.Float3),
    		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Float3),
    		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Float2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleFan;

        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.FLOAT, (int) 3 * (_n + 4), (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.FLOAT, (int) 3 * (_n + 4), (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.FLOAT, (int) 2 * (_n + 4), (BufferFlag[]) null);

        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.getIntBuffer(index_tab), (int) (2 * (_n + 4)), (BufferFlag[]) null);

    }

}
