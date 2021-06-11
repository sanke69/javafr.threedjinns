package fr.threedijnns.objects.space.wrapper.quadrics;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.algebra.vector.generic.Vector2D;
import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.space.shapes.quadrics.shapes.Cylinder3D;
import fr.java.maths.geometry.types.Points;
import fr.java.utils.Buffers3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.gxBuffer;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.space.wrapper.GxAbstractWrapper3D;

public class GxCylinder3D extends GxAbstractWrapper3D<Cylinder3D> {
	public static boolean hasBase = true, hasHat = true;

    protected IDeclaration	m_Declaration;
    protected PrimitiveType	m_PrimitiveType;

    protected gxBuffer		m_TopVertexBuffer;		// <Point3D>
    protected gxBuffer		m_TopNormalBuffer;		// <Vector3D>
    protected gxBuffer		m_TopMapBuffer;			// <Vector2d>
    protected gxBuffer		m_TopIndexBuffer;		// <Integer>

    protected gxBuffer		m_VertexBuffer;			// <Point3D>
    protected gxBuffer		m_NormalBuffer;			// <Vector3D>
    protected gxBuffer		m_MapBuffer;			// <Vector2d>
    protected gxBuffer		m_IndexBuffer;			// <Integer>

    protected gxBuffer		m_BottomVertexBuffer;	// <Point3D>
    protected gxBuffer		m_BottomNormalBuffer;	// <Vector3D>
    protected gxBuffer		m_BottomMapBuffer;		// <Vector2d>
    protected gxBuffer		m_BottomIndexBuffer;	// <Integer>
    
    protected int m_Stacks;
    protected int m_Slices;
    protected int m_BaseSlices; // nb cercles de 0 à r

	public GxCylinder3D(Cylinder3D _model, int _stacks, int _slices) {
		super(_model);

		m_Stacks = _stacks;
		m_Slices = _slices;

        gx.runLater(() -> build(_stacks, _slices));
	}

	@Override
	public void renderModel() {
		gx.setDeclaration(m_Declaration);

		// Render Cone Hat
		gx.setVertexBuffer(0, m_TopVertexBuffer);
//		gx.setVertexBuffer(1, m_TopNormalBuffer);
//		gx.setVertexBuffer(2, m_TopMapBuffer);

		gx.setIndexBuffer(m_TopIndexBuffer);
		gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, m_TopIndexBuffer.getCount());

		// Render Cone Body
		gx.setVertexBuffer(0, m_VertexBuffer);
		gx.setVertexBuffer(1, m_NormalBuffer);
		gx.setVertexBuffer(2, m_MapBuffer);

		gx.setIndexBuffer(m_IndexBuffer);
		gx.drawIndexedPrimitives(m_PrimitiveType, 0, m_IndexBuffer.getCount());

		// Render Cone Base
		gx.setVertexBuffer(0, m_BottomVertexBuffer);
//		gx.setVertexBuffer(1, m_BottomNormalBuffer);
//		gx.setVertexBuffer(2, m_BottomMapBuffer);

		gx.setIndexBuffer(m_BottomIndexBuffer);
		gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, m_BottomIndexBuffer.getCount());
	}

	private void build(int _n, int _m) {
        Point3D[]  topVertex_tab = new Point3D[_m + 2];
        Vector3D[] topNormal_tab = new Vector3D[_m + 2];
        Vector2D[] topMap_tab    = new Vector2D[_m + 2];
        int[]      topIndex_tab  = new int[_m + 2];

        Point3D[]  vertex_tab = new Point3D[(_n + 1) * (_m + 1)];
        Vector3D[] normal_tab = new Vector3D[(_n + 1) * (_m + 1)];
        Vector2D[] map_tab    = new Vector2D[(_n + 1) * (_m + 1)];
        int[]      index_tab  = new int[6 * (_n + 1) * (_m + 1)];

        Point3D[]  bottomVertex_tab = new Point3D[_m + 2];
        Vector3D[] bottomNormal_tab = new Vector3D[_m + 2];
        Vector2D[] bottomMap_tab    = new Vector2D[_m + 2];
        int[]      bottomIndex_tab  = new int[_m + 2];

		int nbStacks = _n + 1;
		int nbSlices = _m;

		double dStacks = /*base.getDepth()*/ 1. / nbStacks;
		double dSlices = 2 * Math.PI / nbSlices;

		double h = 1.0;
		// Top: alt = 0
        topVertex_tab[0] = Points.of(model().getCenter().getX(), model().getCenter().getY(), h);
		for(int i = 0; i < _m + 1; ++i)
			topVertex_tab[i + 1] = model().getSurfacePoint(h, i * dSlices);

		for(int i = 0; i < _m + 2; ++i)
			topIndex_tab[i] = i;
		topIndex_tab[_m + 1] = 1;

		// Body: alt = [ 0, ALT-1]
		for(int i = 0; i < _n + 1; ++i)
			for(int j = 0; j < _m + 1; ++j) {
				vertex_tab[i * (_m + 1) + j] = model().getSurfacePoint((i + 1) * dStacks, j * dSlices);
				normal_tab[i * (_m + 1) + j] = Vectors.of(vertex_tab[i * (_m + 1) + j].normalized());
				map_tab[i * (_m + 1) + j] = Vectors.of((float) j / (_m + 1), (float) i / (_n + 1));
			}

        int index = 0, Top, T, Bottom, B;
        for(int i = 0; i < _n + 1; i++) {
            Top    = i * (_m + 1);
            Bottom = Top + (_m + 1);
            for(int j = 0; j < _m + 1; j++) {
                T = Top + j;
                B = Bottom + j;
                index_tab[index++] = T;     index_tab[index++] = B; index_tab[index++] = T + 1;
                index_tab[index++] = T + 1; index_tab[index++] = B; index_tab[index++] = B + 1;
            }
        }

		// Bottom: alt = [0]
        bottomVertex_tab[0] = model().getSurfacePoint(1, 0);
		for(int i = 0; i < _m + 1; ++i)
			bottomVertex_tab[i + 1] = model().getSurfacePoint((nbStacks - 1) * dStacks, i * dSlices);

		for(int i = 0; i < _m + 2; ++i)
			bottomIndex_tab[i] = i;
		bottomIndex_tab[_m + 1] = 1;


        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
        		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
        		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Double3),
        		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Double2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleList;

        /// Buffer Creation
        m_TopVertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(topVertex_tab), Primitives.DOUBLE, 3 * (_m + 2), (BufferFlag[]) null);
//        m_TopPoleNormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(topNormal_tab), Primitives.DOUBLE, 3 * (_n + 2), (BufferFlag[]) null);
//        m_TopPoleMapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(topMap_tab),    Primitives.DOUBLE, 2 * (_n + 2), (BufferFlag[]) null);
        m_TopIndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(topIndex_tab), _m + 2, (BufferFlag[]) null);

        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, (int) 2 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(index_tab), (int) (6 * (_n + 1) * (_m + 1)), (BufferFlag[]) null);

        m_BottomVertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(bottomVertex_tab), Primitives.DOUBLE, (int) 3 * (_m + 2), (BufferFlag[]) null);
//        m_BasePoleNormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.FLOAT, (int) 3 * (_n + 2), (BufferFlag[]) null);
//        m_BasePoleMapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.FLOAT, (int) 2 * (_n + 2), (BufferFlag[]) null);
        m_BottomIndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(bottomIndex_tab), _m + 2, (BufferFlag[]) null);

    }

}
