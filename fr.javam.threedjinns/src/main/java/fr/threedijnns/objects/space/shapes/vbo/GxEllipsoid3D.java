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

public class GxEllipsoid3D extends AbstractVBObject {

    protected int m_Stacks;
    protected int m_Slices;

	public GxEllipsoid3D(float _w, float _h, float _d, int _stacks, int _slices) {
		super(_w, _h, _d);
		m_Stacks = _stacks;
		m_Slices = _slices;

        gx.runLater(() -> build(_stacks, _slices));
	}
	public GxEllipsoid3D(double _w, double _h, double _d, int _stacks, int _slices) {
		super(_w, _h, _d);
		m_Stacks = _stacks;
		m_Slices = _slices;

        gx.runLater(() -> build(_stacks, _slices));
	}

	@Override
	public void process() {
		;
	}
	@Override
	public void setDeclarationAndBuffers() {
		if(m_VertexBuffer == null)
			return;

		gx.setDeclaration(m_Declaration);

        gx.setVertexBuffer(0, m_VertexBuffer);
        gx.setVertexBuffer(1, m_NormalBuffer);
        gx.setVertexBuffer(2, m_MapBuffer);

        gx.setIndexBuffer(m_IndexBuffer);
        gx.drawIndexedPrimitives(m_PrimitiveType, 0, m_IndexBuffer.getCount());
	}

	private void build(int _n, int _m) {
        float	width  = (float) boundBox.getWidth(),
                height = (float) boundBox.getHeight(),
                dDir   = (float) (2.0f * Math.PI / m_Slices),
                dElev  = (float) (       Math.PI / m_Stacks),
                dU     = 1.0f / m_Slices,
                dV     = 1.0f / m_Stacks,
                Dir    = 0.0f,
                Elev   = (float) Math.PI / 2.0f,
                S_Elev = 0.0f,
                U      = 1.0f,
                V      = 1.0f,
                Ray    = 0.0f;
        int     Top    = 0,
                Bottom = 0,
                T      = 0,
                B      = 0;

        /// Création des Vertex
        Point3D[]  vertex_tab = new Point3D[(_n + 1) * (_m + 1)];
        Vector3D[] normal_tab = new Vector3D[(_n + 1) * (_m + 1)];
        Point2D[]     map_tab = new Point2D[(_n + 1) * (_m + 1)];
        int[]       index_tab = new int[6 * (_n + 1) * (_m + 1)];
/*
        for(int i = 0; i < (_n + 1) * (_m + 1); ++i) {
            vertex_tab[i] = new Vector3D.Double();
            normal_tab[i] = new Vector3D.Double();
            map_tab[i]    = new Vector2D.Double();
        }
*/
        int index = 0;
        for(int i = 0; i < _n + 1; i++, Elev -= dElev, V -= dV) {
            Dir    = 0;
            U      = 1;
            S_Elev = (1 + (float) Math.sin(Elev)) * width / 2.0f;
            Ray    = (float) Math.cos(Elev) * height / 2.0f;
            Top    = i * _m + 1;
            Bottom = Top + _m + 1;

            // dans le plan x/z, => de la droite vers la gauche au niveau de la texture
            for(int j = 0 ; j < _m + 1 ; j++, U -= dU, Dir += dDir, index++) {
            	float vx = width / 2.0f + (float) Math.cos(Dir) * Ray;
            	float vy = S_Elev;
            	float vz = height / 2.0f - (float) Math.sin(Dir) * Ray;
            	float nx = - (float) Math.cos((float) (Dir + Math.PI / 2.0f));
            	float ny = 0.0f;
            	float nz = - (float) Math.sin((float) (Dir + Math.PI / 2.0f));
 
                vertex_tab[index]	= Points.of(vx, vy, vz);
                normal_tab[index]	= Vectors.of(nx, ny, nz);
                map_tab[index]		= Points.of(U, V);

            }
        }

        index = 0;
        for(int i = 0; i < _n; i++) {
            Top    = i * (_m + 1);
            Bottom = Top + (_m + 1);
            for(int j = 0; j < _m; j++) {
                T = Top + j;
                B = Bottom + j;
                index_tab[index++] = T;     index_tab[index++] = B; index_tab[index++] = T + 1;
                index_tab[index++] = T + 1; index_tab[index++] = B; index_tab[index++] = B + 1;
            }
        }

        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
        		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
        		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Double3),
        		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Double2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleList;

        /// Buffer Creation
        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, (int) 2 * (_n + 1) * (_m + 1), (BufferFlag[]) null);

        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(index_tab), (int) (6 * (_n + 1) * (_m + 1)), (BufferFlag[]) null);

    }

}
