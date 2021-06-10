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

public class GxBrick3D extends AbstractVBObject {

    protected int m_Precision;

	public GxBrick3D(float _w, float _h, float _d, int _p) {
		super(_w, _h, _d);
		m_Precision = _p;

		gx.runLater(() -> build(m_Precision));
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
        gx.setVertexBuffer(2, m_MapBuffer);

        gx.setIndexBuffer(m_IndexBuffer);

        gx.drawIndexedPrimitives(m_PrimitiveType, 0, m_IndexBuffer.getCount());

		gx.setDeclaration(null);
    }

    private void build(int _n) {
        float	x0 = 0.0f,
                y0 = 0.0f,
                z0 = 0.0f,
                x1 = (float) boundBox.getWidth(),
                y1 = (float) boundBox.getHeight(),
                z1 = (float) boundBox.getDepth();

        float	dx = (float) boundBox.getWidth()  / m_Precision,
                dy = (float) boundBox.getHeight() / m_Precision,
                dz = (float) boundBox.getDepth()  / m_Precision,
                du = 1.0f / m_Precision,
                dv = 1.0f / m_Precision;

        int     index, i, j, k,
                n  = (_n + 1) * (_n + 1),
                ni = 6 * _n * _n;

        /// Vertices Creation
        Point3D[]  vertex_tab = new Point3D[6 * n];
        Vector3D[] normal_tab = new Vector3D[6 * n];
        Point2D[]     map_tab = new Point2D[6 * n];
        int[]       index_tab = new int[6 * ni];
/*
        for(i = 0; i < 6 * n; ++i) {
            vertex_tab[i] = Vector3D.zero();
            normal_tab[i] = Vector3D.zero();
            map_tab[i]    = Vector2D.zero();
        }
*/
        index = 0;
        for(j = 0; j < (_n + 1); j++) {
            for(i = 0; i < (_n + 1); i++) {
                for(k = 0; k < 6; k++) {
                    index = k * n + i + j * (_n + 1);
                    switch(k) {
                        case 0    : vertex_tab[index] = Points.of(x0, y0 + i * dy, z0 + j * dz);
                                    normal_tab[index] = Vectors.of(- 1.0f, 0.0f, 0.0f);
                                    break;

                        case 1    : vertex_tab[index] = Points.of(x1, y0 + i * dy, z0 + j * dz);
                                    normal_tab[index] = Vectors.of(1.0f, 0.0f, 0.0f);
                                    break;

                        case 2    : vertex_tab[index] = Points.of(x0 + i * dx, y0, z0 + j * dz);
                                    normal_tab[index] = Vectors.of(0.0f, -1.0f, 0.0f);
                                    break;

                        case 3    : vertex_tab[index] = Points.of(x0 + i * dx, y1, z0 + j * dz);
                                    normal_tab[index] = Vectors.of(0.0f, 1.0f, 0.0f);
                                    break;

                        case 4    : vertex_tab[index] = Points.of(x0 + i * dx, y0 + j * dy, z0);
                                    normal_tab[index] = Vectors.of(0.0f, 0.0f, -1.0f);
                                    break;

                        case 5    : vertex_tab[index] = Points.of(x0 + i * dx, y0 + j * dy, z1);
                                    normal_tab[index] = Vectors.of(0.0f, 0.0f, 1.0f);
                                    break;
                    }
                    map_tab[index] = Points.of( i * du , j * dv );
                }
            }
        }

        index = 0;
        for(k = 0; k < 6; k++) {

            for(i = 0; i < _n; i++) {
                int Top    = k * n + i * (_n + 1);
                int Bottom = Top + (_n + 1);
                for(j = 0; j < _n; j++) {
                    int T = Top + j;
                    int B = Bottom + j;
                    index_tab[index++] = T;     index_tab[index++] = B; index_tab[index++] = T + 1;
                    index_tab[index++] = T + 1; index_tab[index++] = B; index_tab[index++] = B + 1;
                }
            }

        }

        /// Declaration
        m_Declaration = gx.createDeclaration(new DeclarationElement[] {
    		new DeclarationElement(0, ElementUsage.Position,  ElementType.Float3),
    		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Float3),
    		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Float2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleList;

        /// Buffer Creation
        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.FLOAT,   (int) 3 * (6 * n), (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.FLOAT,   (int) 3 * (6 * n), (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.FLOAT,   (int) 2 * (6 * n), (BufferFlag[]) null);

        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(index_tab), (int) index_tab.length, (BufferFlag[]) null);

    }

}
