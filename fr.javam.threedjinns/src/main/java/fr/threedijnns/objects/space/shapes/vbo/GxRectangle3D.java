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

public class GxRectangle3D extends AbstractVBObject {

    protected int m_Precision;

	public GxRectangle3D(float _w, float _h, int _pw, int _ph) {
		super(_w, _h, 0);
		m_Precision = 10; //_pw;
	}

	@Override
	public void process() {
		if(m_VertexBuffer == null || m_IndexBuffer == null)
			build(m_Precision);
	}
	@Override
	public void setDeclarationAndBuffers() {
		if(m_VertexBuffer == null || m_IndexBuffer == null)
			return ;

		gx.setDeclaration(m_Declaration);

        gx.setVertexBuffer(0, m_VertexBuffer);
        gx.setVertexBuffer(1, m_NormalBuffer);
        gx.setVertexBuffer(2, m_MapBuffer);

        gx.setIndexBuffer(m_IndexBuffer);

        gx.drawIndexedPrimitives(m_PrimitiveType, 0, m_IndexBuffer.getCount());

		gx.setDeclaration(null);
    }

    private void build(int _n) {
        float	x0 = 0.0f,
                y0 = 0.0f,
                z0 = 0.0f;

        float	dx = (float) boundBox.getWidth()  / m_Precision,
                dy = (float) boundBox.getHeight() / m_Precision,
                du = 1.0f / m_Precision,
                dv = 1.0f / m_Precision;

        int     index, j, i,
                n  = (_n + 1) * (_n + 1),
                ni = 6 * _n * _n;

        /// Vertices Creation
        Point3D[]  vertex_tab = new Point3D[n];
        Vector3D[] normal_tab = new Vector3D[n];
        Point2D[]     map_tab = new Point2D[n];
        int[]       index_tab = new int[ni];

        index = 0;
        for(j = 0; j < (_n + 1); ++j)
            for(i = 0; i < (_n + 1); ++i) {
                index = i + j * (_n + 1);

                vertex_tab[index] = Points.of(x0 + i * dx, y0 + j * dy, z0);
                normal_tab[index] = Vectors.of(0.0f, 0.0f, 1.0f);
                map_tab[index]    = Points.of(i * du, (_n - j) * dv);
            }

        index = 0;
        for(j = 0; j < _n; j++) {
            int Bottom = j * (_n + 1);
            int Top    = Bottom + (_n + 1);
            for(i = 0; i < _n; i++) {
                int T = Top + i;
                int B = Bottom + i;
                index_tab[index++] = T;     index_tab[index++] = B; index_tab[index++] = T + 1;
                index_tab[index++] = T + 1; index_tab[index++] = B; index_tab[index++] = B + 1;
            }
        }

        /// Declaration
        m_Declaration = gx.createDeclaration(new DeclarationElement[] {
    		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
    		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Double3),
    		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Double2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleList;

        /// Buffer Creation
        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.DOUBLE,   (int) 3 * n, (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE,   (int) 3 * n, (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE,   (int) 2 * n, (BufferFlag[]) null);

        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.getIntBuffer(index_tab), (int) index_tab.length, (BufferFlag[]) null);
    }

}
