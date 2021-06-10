package fr.threedijnns.objects.space.wrapper.quadrics;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.plane.Vector2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.space.shapes.quadrics.shapes.Cone3D;
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

public class GxCone3D extends GxAbstractWrapper3D<Cone3D> {
	public static boolean hasBase = true, hasHat = true;

    protected IDeclaration	m_Declaration;
    protected PrimitiveType	m_PrimitiveType;

    protected gxBuffer		m_HatVertexBuffer;		// <Point3D>
    protected gxBuffer		m_HatNormalBuffer;		// <Vector3D>
    protected gxBuffer		m_HatMapBuffer;			// <Vector2d>
    protected gxBuffer		m_HatIndexBuffer;		// <Integer>

    protected gxBuffer		m_VertexBuffer;			// <Point3D>
    protected gxBuffer		m_NormalBuffer;			// <Vector3D>
    protected gxBuffer		m_MapBuffer;			// <Vector2d>
    protected gxBuffer		m_IndexBuffer;			// <Integer>

    protected gxBuffer		m_BaseVertexBuffer;		// <Point3D>
    protected gxBuffer		m_BaseNormalBuffer;		// <Vector3D>
    protected gxBuffer		m_BaseMapBuffer;		// <Vector2d>
    protected gxBuffer		m_BaseIndexBuffer;		// <Integer>
    
    protected int m_Stacks;
    protected int m_Slices;
    protected int m_BaseSlices; // nb cercles de 0 à r

	public GxCone3D(Cone3D _model, int _stacks, int _slices) {
		super(_model);

		m_Stacks = _stacks;
		m_Slices = _slices;

        gx.runLater(() -> build(_stacks, _slices));
	}

	@Override
	public void renderModel() {
		gx.setDeclaration(m_Declaration);

		// Render Cone Hat
		gx.setVertexBuffer(0, m_HatVertexBuffer);
//		gx.setVertexBuffer(1, m_HatNormalBuffer);
//		gx.setVertexBuffer(2, m_HatMapBuffer);

		gx.setIndexBuffer(m_HatIndexBuffer);
		gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, m_HatIndexBuffer.getCount());

		// Render Cone Body
		gx.setVertexBuffer(0, m_VertexBuffer);
		gx.setVertexBuffer(1, m_NormalBuffer);
		gx.setVertexBuffer(2, m_MapBuffer);

		gx.setIndexBuffer(m_IndexBuffer);
		gx.drawIndexedPrimitives(m_PrimitiveType, 0, m_IndexBuffer.getCount());

		// Render Cone Base
		gx.setVertexBuffer(0, m_BaseVertexBuffer);
//		gx.setVertexBuffer(1, m_BaseNormalBuffer);
//		gx.setVertexBuffer(2, m_BaseMapBuffer);

		gx.setIndexBuffer(m_BaseIndexBuffer);
		gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, m_BaseIndexBuffer.getCount());
	}

	private void build(int _n, int _m) {
        Point3D[] hatVertex_tab = new Point3D[_m + 2];
        Point3D[] hatNormal_tab = new Point3D[_m + 2];
        Vector2D[] hatMap_tab   = new Vector2D[_m + 2];
        int[]     hatIndex_tab  = new int[_m + 2];

        Point3D[]  vertex_tab = new Point3D[(_n + 1) * (_m + 1)];
        Vector3D[] normal_tab = new Vector3D[(_n + 1) * (_m + 1)];
        Vector2D[] map_tab    = new Vector2D[(_n + 1) * (_m + 1)];
        int[]      index_tab  = new int[6 * (_n + 1) * (_m + 1)];

        Point3D[] baseVertex_tab = new Point3D[_m + 2];
        Point3D[] baseNormal_tab = new Point3D[_m + 2];
        Point2D[] baseMap_tab    = new Point2D[_m + 2];
        int[]     baseIndex_tab  = new int[_m + 2];

		int nbStacks = _n + 1;
		int nbSlices = _m;

		double dStacks = /*base.getDepth()*/ 1. / nbStacks;
		double dSlices = 2 * Math.PI / nbSlices;

		// Base: alt = 0
        baseVertex_tab[0] = model().getSurfacePoint(0, 0);
		for(int i = 0; i < _m + 1; ++i)
			baseVertex_tab[i + 1] = model().getSurfacePoint(0, i * dSlices);

		for(int i = 0; i < _m + 2; ++i)
			baseIndex_tab[i] = i;
		baseIndex_tab[_m + 1] = 1;

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

		// Hat: alt = [ALT]
        hatVertex_tab[0] = model().getSurfacePoint(1, 0);
		for(int i = 0; i < _m + 1; ++i)
			hatVertex_tab[i + 1] = model().getSurfacePoint((nbStacks - 1) * dStacks, i * dSlices);

		for(int i = 0; i < _m + 2; ++i)
			hatIndex_tab[i] = i;
		hatIndex_tab[_m + 1] = 1;


        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
        		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
        		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Double3),
        		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Double2)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleList;

        /// Buffer Creation
        m_HatVertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(hatVertex_tab), Primitives.DOUBLE, 3 * (_m + 2), (BufferFlag[]) null);
//        m_HatPoleNormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE, 3 * (_n + 2), (BufferFlag[]) null);
//        m_HatPoleMapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, 2 * (_n + 2), (BufferFlag[]) null);
        m_HatIndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(hatIndex_tab), _m + 2, (BufferFlag[]) null);

        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_NormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, (int) 2 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(index_tab), (int) (6 * (_n + 1) * (_m + 1)), (BufferFlag[]) null);

        m_BaseVertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(baseVertex_tab), Primitives.DOUBLE, (int) 3 * (_m + 2), (BufferFlag[]) null);
//        m_BasePoleNormalBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE, (int) 3 * (_n + 2), (BufferFlag[]) null);
//        m_BasePoleMapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, (int) 2 * (_n + 2), (BufferFlag[]) null);
        m_BaseIndexBuffer   = gx.createIndexBuffer(Buffers3D.asBuffer(baseIndex_tab), _m + 2, (BufferFlag[]) null);

    }

}
