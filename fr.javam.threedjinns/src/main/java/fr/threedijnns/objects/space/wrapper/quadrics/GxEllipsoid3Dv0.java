package fr.threedijnns.objects.space.wrapper.quadrics;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.algebra.vector.generic.Vector2D;
import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.shapes.Ellipsoid3D;
import fr.java.maths.algebra.Vectors;
import fr.java.utils.Buffers3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.GxVBO;
import fr.threedijnns.api.lang.buffer.gxBuffer;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.space.wrapper.GxAbstractWrapper3D;

public class GxEllipsoid3Dv0 extends GxAbstractWrapper3D<Ellipsoid3D> implements GxVBO {
    protected IDeclaration	declaration;
    protected PrimitiveType	primitiveType;

    protected gxBuffer		northPoleVB;				// <Point3D>
    protected gxBuffer		northPoleNB;				// <Vector3D>
    protected gxBuffer		northPoleMB;				// <Vector2d>
    protected gxBuffer		northPoleIB;				// <Integer>

    protected gxBuffer		defaultVB;					// <Point3D>
    protected gxBuffer		defaultNB;					// <Vector3D>
    protected gxBuffer		defaultMB;					// <Vector2d>
    protected gxBuffer		defaultIB;					// <Integer>

    protected gxBuffer		southPoleVB;				// <Point3D>
    protected gxBuffer		southPoleNB;				// <Vector3D>
    protected gxBuffer		southPoleMB;				// <Vector2d>
    protected gxBuffer		southPoleIB;				// <Integer>

    protected int 			stacks, slices;

	public GxEllipsoid3Dv0(Ellipsoid3D _model, int _stacks, int _slices) {
		super(_model);

		stacks = _stacks;
		slices = _slices;

		build();
	}

	@Override
	public IDeclaration getDeclaration() {
		return declaration;
	}

	@Override
	public void 		buildVBOs() {
		build(stacks, slices);
	}

	public void 		renderModel() {
//		if(m_VertexBuffer == null)
//			return;

		gx.setDeclaration(declaration);

		// Render North Pole
//        gx.setVertexBuffer(0, northPoleVB);
//        gx.setIndexBuffer(northPoleIB);
//        gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, northPoleIB.getCount());
		
		// Render Plane Sphere
        gx.setVertexBuffer(0, defaultVB);
        gx.setVertexBuffer(1, defaultNB);
        gx.setVertexBuffer(2, defaultMB);

        gx.setIndexBuffer(defaultIB);
        gx.drawIndexedPrimitives(primitiveType, 0, defaultIB.getCount());

		// Render South Pole
//        gx.setVertexBuffer(0, southPoleVB);
//        gx.setIndexBuffer(southPoleIB);
//        gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, southPoleIB.getCount());
	}

	private void 		build(int _n, int _m) {
		int    nbStacks = _n + 2;
		int    nbSlices = _m;
		double dStacks  =     Math.PI / nbStacks;
		double dSlices  = 2 * Math.PI / nbSlices;

		// Build North Pole
		// ----------------
        Point3D[]  northVertex = new Point3D[_m + 2];
        Vector3D[] northNormal = new Vector3D[_m + 2];
        Point2D[]  northMap    = new Point2D[_m + 2];
        int[]      northIndex  = new int[_m + 2];

        northVertex[0] = model().getSurfacePoint(0 * dStacks, 0);
		for(int i = 0; i < _m + 1; ++i)
			northVertex[i + 1] = model().getSurfacePoint(1 * dStacks, i * dSlices);

		for(int i = 0; i < _m + 2; ++i)
			northIndex[i] = i;
		northIndex[_m + 1] = 1;

		// Build South Pole
		// ----------------
        Point3D[]  southVertex = new Point3D[_m + 2];
        Point3D[]  southNormal = new Point3D[_m + 2];
        Point2D[]  southMap    = new Point2D[_m + 2];
        int[]      southIndex  = new int[_m + 2];

		southVertex[0] = model().getSurfacePoint(nbStacks * dStacks, 0);
		for(int i = 0; i < _m + 1; ++i)
			southVertex[i + 1] = model().getSurfacePoint((nbStacks - 1) * dStacks, i * dSlices);

		for(int i = 0; i < _m + 2; ++i)
			southIndex[i] = i;
		southIndex[_m + 1] = 1;


		// Build Main Shape
		// ----------------
        Point3D[]  vertex_tab  = new Point3D[(_n + 1) * (_m + 1)];
        Vector3D[] normal_tab  = new Vector3D[(_n + 1) * (_m + 1)];
        Vector2D[] map_tab     = new Vector2D[(_n + 1) * (_m + 1)];
        int[]      index_tab   = new int[6 * (_n + 1) * (_m + 1)];

		for(int i = 0; i < _n + 1; ++i)
			for(int j = 0; j < _m + 1; ++j) {
				int index = i * (_m + 1) + j;
				
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

        /// Declaration
        declaration	= gx.createDeclaration(new DeclarationElement[] {
        		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
        		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Double3),
        		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Double2)
        });

        /// Primitive Type
        primitiveType = PrimitiveType.TriangleList;

        /// Buffer Creation
        northPoleVB = gx.createVertexBuffer(Buffers3D.asBuffer(northVertex), Primitives.DOUBLE, 3 * (_m + 2), (BufferFlag[]) null);
//      northPoleNB = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), BufferPrimitive.FLOAT, 3 * (_n + 2), (BufferFlag[]) null);
//      northPoleMB = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    BufferPrimitive.FLOAT, 2 * (_n + 2), (BufferFlag[]) null);
        northPoleIB = gx.createIndexBuffer(Buffers3D.asBuffer(northIndex), _m + 2, (BufferFlag[]) null);

        southPoleVB = gx.createVertexBuffer(Buffers3D.asBuffer(southVertex), Primitives.DOUBLE, (int) 3 * (_m + 2), (BufferFlag[]) null);
//      southPoleNB = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), BufferPrimitive.FLOAT, (int) 3 * (_n + 2), (BufferFlag[]) null);
//      southPoleMB = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    BufferPrimitive.FLOAT, (int) 2 * (_n + 2), (BufferFlag[]) null);
        southPoleIB = gx.createIndexBuffer(Buffers3D.asBuffer(southIndex), _m + 2, (BufferFlag[]) null);

        defaultVB   = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        defaultNB   = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.DOUBLE, (int) 3 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        defaultMB   = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, (int) 2 * (_n + 1) * (_m + 1), (BufferFlag[]) null);
        defaultIB   = gx.createIndexBuffer(Buffers3D.asBuffer(index_tab), (int) (6 * (_n + 1) * (_m + 1)), (BufferFlag[]) null);

    }

}
