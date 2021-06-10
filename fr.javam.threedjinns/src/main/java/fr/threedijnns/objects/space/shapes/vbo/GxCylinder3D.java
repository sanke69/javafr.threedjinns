package fr.threedijnns.objects.space.shapes.vbo;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.Points;
import fr.java.maths.algebra.Vectors;
import fr.java.utils.Buffers3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public class GxCylinder3D extends AbstractVBObject {

	protected int m_Precision;

	public GxCylinder3D(float _w, float _h, float _d, int _p) {
		super(_w, _h, _d);
		m_Precision = _p;

        build(_p);
	}

	@Override
	public void process() {
	}
	@Override
	public void setDeclarationAndBuffers() {
		if(m_VertexBuffer == null)
			return;

        int m_NbCircleVertex 	 = m_Precision + 2,
	        m_NbVertex           = 2 * m_NbCircleVertex + 2,
	        m_NbIndex            = 4 * m_NbCircleVertex + 6,
	        m_NbTriangleTop      = m_NbCircleVertex + 1,
	        m_NbTriangleCylinder = 2 * (m_NbCircleVertex + 1),
	        m_NbTriangleBottom   = m_NbCircleVertex + 1,
	        m_1stIndexBase       = m_NbCircleVertex + 2,
	        m_1stIndexCylinder   = 2 * m_NbCircleVertex + 4;

		gx.setDeclaration(m_Declaration);

        gx.setVertexBuffer(0, m_VertexBuffer);
        gx.setVertexBuffer(1, m_NormalBuffer);

        gx.setIndexBuffer(m_IndexBuffer);
        gx.drawIndexedPrimitives(PrimitiveType.TriangleFan,   0,                  m_NbTriangleTop + 1);
        gx.drawIndexedPrimitives(PrimitiveType.TriangleStrip, m_1stIndexCylinder, m_NbTriangleCylinder);
        gx.drawIndexedPrimitives(PrimitiveType.TriangleFan,   m_1stIndexBase,     m_NbTriangleBottom + 1);

	}

	private void build(int _n) {
        int m_NbCircleVertex     = m_Precision + 2,
            m_NbVertex           = 2 * m_NbCircleVertex + 2,
            m_NbIndex            = 4 * m_NbCircleVertex + 6,
            m_NbTriangleTop      = m_NbCircleVertex + 1,
            m_NbTriangleCylinder = 2 * (m_NbCircleVertex + 1),
            m_NbTriangleBottom   = m_NbCircleVertex + 1,
            m_1stIndexBase       = m_NbCircleVertex + 2,
            m_1stIndexCylinder   = 2 * m_NbCircleVertex + 4;

        float	width  = (float) boundBox.getWidth(),
                height = (float) boundBox.getHeight();
        float	z0     = 0,
                z1     = (float) boundBox.getDepth();
        float  du     = 1.0f / m_NbCircleVertex;

        float	anglestep = (float) (2.0f * Math.PI / m_NbCircleVertex);
        float	ca, sa;

        Point3D[]  vertex_tab = new Point3D[m_NbVertex];
        Vector3D[] normal_tab = new Vector3D[m_NbVertex];
        Point2D[]     map_tab = new Point2D[m_NbVertex];
        int[]       index_tab = new int[m_NbIndex];
/*
        for(int i = 0; i < m_NbVertex; ++i) {
            vertex_tab[i] = new Vector3D.Double();
            normal_tab[i] = new Vector3D.Double();
            map_tab[i]    = new Vector2D.Double();
        }
*/
        for(int i = 0; i < m_NbCircleVertex; i++) {
            ca = (float) Math.cos(i * anglestep);
            sa = (float) Math.sin(i * anglestep);

            vertex_tab[i]						 = Points.of(width  * (1 + ca)  / 2.0f, height * (1 + sa)  / 2.0f, z0);
            vertex_tab[m_NbCircleVertex + 1 + i] = Points.of(width  * (1 + ca)  / 2.0f, height * (1 + sa)  / 2.0f, z1);

            normal_tab[i]					 	 = Vectors.of(ca, sa, 0.0f);
            normal_tab[m_NbCircleVertex + 1 + i] = Vectors.of(ca, sa, 0.0f);

            map_tab[i]							 = Points.of(i * du, 0.0f);
            map_tab[m_NbCircleVertex + 1 + i]	 = Points.of(i * du, 1.0f);
        }
        vertex_tab[m_NbCircleVertex]			 = Points.of(width  / 2.0f, height / 2.0f, z0);
        vertex_tab[2 * m_NbCircleVertex + 1]	 = Points.of(width  / 2.0f, height / 2.0f, z1);

        normal_tab[m_NbCircleVertex]			 = Vectors.of(0.0f, 0.0f, -1.0f);
        normal_tab[2 * m_NbCircleVertex + 1]	 = Vectors.of(0.0f, 0.0f,  1.0f);

        map_tab[m_NbCircleVertex]				 = Points.of(0.5f, 0.5f);
        map_tab[2 * m_NbCircleVertex + 1]		 = Points.of(0.5f, 0.5f);

        /// Création des Indices
        index_tab[0] = m_NbCircleVertex;
        for(int i = 0; i < m_NbCircleVertex; i++)
            index_tab[i + 1] = i;
        index_tab[m_NbCircleVertex + 1] = 0;

        index_tab[m_1stIndexBase] = 2 * m_NbCircleVertex + 1;
        for(int i = 0; i < m_NbCircleVertex; i++)
            index_tab[m_1stIndexBase + i + 1] = m_NbCircleVertex + 1 + i;
        index_tab[m_1stIndexBase + m_NbCircleVertex + 1] = m_NbCircleVertex + 1;

        for(int i = 0; i < m_NbCircleVertex; i++) {
            index_tab[m_1stIndexCylinder + 2 * i + 0] = i;
            index_tab[m_1stIndexCylinder + 2 * i + 1] = m_NbCircleVertex + 1 + i;
        }
        index_tab[m_1stIndexCylinder + 2 * m_NbCircleVertex + 0] = 0;
        index_tab[m_1stIndexCylinder + 2 * m_NbCircleVertex + 1] = m_NbCircleVertex + 1;

        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
        		new DeclarationElement(0, ElementUsage.Position,  ElementType.Float3),
        		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Float3),
        		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Float2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleFan;

        /// Création du buffer
        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.FLOAT, (int) (3 * m_NbVertex), (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.FLOAT, (int) (3 * m_NbVertex), (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.FLOAT, (int) (2 * m_NbVertex), (BufferFlag[]) null);

        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(index_tab), (int) (m_NbIndex), (BufferFlag[]) null);

	}

}
