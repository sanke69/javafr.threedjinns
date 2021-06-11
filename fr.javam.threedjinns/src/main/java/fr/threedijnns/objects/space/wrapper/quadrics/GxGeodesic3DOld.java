package fr.threedijnns.objects.space.wrapper.quadrics;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.algebra.vector.generic.Vector2D;
import fr.java.math.algebra.vector.generic.Vector3D;
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

public class GxGeodesic3DOld extends GxAbstractWrapper3D<Ellipsoid3D> implements GxVBO {
    protected IDeclaration	declaration;
    protected PrimitiveType	primitiveType;

    protected gxBuffer		planeVB;					// <Point3D>
    protected gxBuffer		planeNB;					// <Vector3D>
    protected gxBuffer		planeMB;					// <Vector2D>
    protected gxBuffer		planeIB;					// <Integer>

    protected double		rho_min, rho_max, phi_min, phi_max;
    protected int 			stacks, slices;

	public GxGeodesic3DOld(Ellipsoid3D _model, int _stacks, int _slices) {
		super(_model);

		stacks = _stacks;
		slices = _slices;

		build();
	}

	public void set(double _rho_min, double _rho_max, double _phi_min, double _phi_max, int _stacks, int _slices) {
		rho_min  = _rho_min;
		rho_max  = _rho_max;
		phi_min = _phi_min;
		phi_max = _phi_max;

		stacks = _stacks;
		slices = _slices;

		if(planeVB != null)
			gx.deleteVertexBuffer(planeVB);
		if(planeNB != null)
			gx.deleteVertexBuffer(planeNB);
		if(planeMB != null)
			gx.deleteVertexBuffer(planeMB);
		if(planeIB != null)
			gx.deleteIndexBuffer(planeIB);

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
		if(planeVB == null)
			return;

		gx.setDeclaration(declaration);

		// Render Plane Sphere
        gx.setVertexBuffer(0, planeVB);
        gx.setVertexBuffer(1, planeNB);
        gx.setVertexBuffer(2, planeMB);

        gx.setIndexBuffer(planeIB);
        gx.drawIndexedPrimitives(primitiveType, 0, planeIB.getCount());

	}

	private void 		build(int _nbStacks, int _nbSlices) {
		double dSlices  = (phi_max - phi_min) / _nbSlices;
		double dStacks  = (rho_max - rho_min) / (_nbStacks + 2);

		// Build Main Shape
		// ----------------
		int        nbPoints    = (_nbStacks + 1) * (_nbSlices + 1);
        Point3D[]  coreVertex  = new Point3D[nbPoints];
        Vector3D[] coreNormal  = new Vector3D[nbPoints];
        Vector2D[] coreMap     = new Vector2D[nbPoints];
        int[]      coreIndex   = new int[6 * _nbStacks * _nbSlices];

		for(int i = 0; i < _nbStacks + 1; ++i)
			for(int j = 0; j < _nbSlices + 1; ++j) {
				int index = i * (_nbSlices + 1) + j;

				coreVertex[index] = model().getSurfacePoint(rho_min + (i + 1) * dStacks, phi_min + j * dSlices);
				coreNormal[index] = Vectors.of(coreVertex[i * (_nbSlices + 1) + j].normalized());
				coreMap[index]    = Vectors.of((float) j / (_nbSlices + 1), (float) i / (_nbStacks + 1));
			}

        int index = 0, Top, T, Bottom, B;
        for(int i = 0; i < _nbStacks; i++) {
            Top    = i * (_nbSlices + 1);
            Bottom = Top + (_nbSlices + 1);
            for(int j = 0; j < _nbSlices; j++) {
                T = Top + j;
                B = Bottom + j;
                coreIndex[index++] = T;     coreIndex[index++] = B; coreIndex[index++] = T + 1;
                coreIndex[index++] = T + 1; coreIndex[index++] = B; coreIndex[index++] = B + 1;
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
        planeVB   = gx.createVertexBuffer(Buffers3D.asBuffer(coreVertex), Primitives.DOUBLE, (int) 3 * nbPoints, (BufferFlag[]) null);
        planeNB   = gx.createVertexBuffer(Buffers3D.asBuffer(coreNormal), Primitives.DOUBLE, (int) 3 * nbPoints, (BufferFlag[]) null);
        planeMB   = gx.createVertexBuffer(Buffers3D.asBuffer(coreMap),    Primitives.DOUBLE, (int) 2 * nbPoints, (BufferFlag[]) null);
        planeIB   = gx.createIndexBuffer(Buffers3D.asBuffer(coreIndex), (int) (6 * _nbStacks * _nbSlices), (BufferFlag[]) null);
    }

}
