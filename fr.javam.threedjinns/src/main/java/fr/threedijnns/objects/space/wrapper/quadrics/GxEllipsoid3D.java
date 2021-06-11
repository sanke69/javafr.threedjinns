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
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.space.wrapper.GxAbstractWrapper3D;
import fr.threedijnns.utils.HeightMap;

public class GxEllipsoid3D extends GxAbstractWrapper3D<Ellipsoid3D> implements GxVBO {
    protected IDeclaration	declaration;
    protected PrimitiveType	primitiveType;

    protected gxBuffer		northPoleVB;				// <Point3D>
    protected gxBuffer		northPoleNB;				// <Vector3D>
    protected gxBuffer		northPoleMB;				// <Vector2D>
    protected gxBuffer		northPoleIB;				// <Integer>

    protected gxBuffer		planeVB;					// <Point3D>
    protected gxBuffer		planeNB;					// <Vector3D>
    protected gxBuffer		planeMB;					// <Vector2D>
    protected gxBuffer		planeIB;					// <Integer>

    protected gxBuffer		southPoleVB;				// <Point3D>
    protected gxBuffer		southPoleNB;				// <Vector3D>
    protected gxBuffer		southPoleMB;				// <Vector2D>
    protected gxBuffer		southPoleIB;				// <Integer>

    protected int 			stacks, slices;
    protected HeightMap		heightMap;

	public GxEllipsoid3D(Ellipsoid3D _model, int _stacks, int _slices) {
		super(_model);

		stacks = _stacks;
		slices = _slices;

		build();
	}
	public GxEllipsoid3D(Ellipsoid3D _model, int _stacks, int _slices, HeightMap _hMap) {
		super(_model);

		stacks    = _stacks;
		slices    = _slices;
		heightMap = _hMap;

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
		gx.setPolygonMode(polygonMode, faceType);
		gx.setDeclaration(declaration);

		// Render North Pole
		if(northPoleVB != null) {
			gx.setVertexBuffer(0, northPoleVB);
//			gx.setVertexBuffer(1, northPoleNB);
	        gx.setVertexBuffer(2, northPoleMB);
	
//	        gx.setIndexBuffer(northPoleIB);
//	        gx.drawIndexedPrimitives(PrimitiveType.TriangleFan, 0, northPoleIB.getCount());
//	        gx.drawPrimitives(PrimitiveType.TriangleFan, 0, slices + 2);
	        gx.drawPrimitives(PrimitiveType.TriangleFan, 0, slices + 2);
		}

		// Render Plane Sphere
		if(planeVB != null) {
			gx.setVertexBuffer(0, planeVB);
//			gx.setVertexBuffer(1, planeNB);
			gx.setVertexBuffer(2, planeMB);
	
			gx.setIndexBuffer(planeIB);
			gx.drawIndexedPrimitives(PrimitiveType.TriangleList, 0, planeIB.getCount());
		}

		// Render South Pole
		if(southPoleVB != null) {
			gx.setVertexBuffer(0,southPoleVB);
//			gx.setVertexBuffer(1, southPoleNB);
			gx.setVertexBuffer(2,southPoleMB);
		
//			gx.setIndexBuffer(southPoleIB);
//			gx.drawIndexedPrimitives(PrimitiveType.TriangleFan,0,southPoleIB.getCount());
	        gx.drawPrimitives(PrimitiveType.TriangleFan, 0, slices + 2);
		}
	}

	private void 		build(int _nbStacks, int _nbSlices) {
        double RhoMin = - Math.PI / 2, RhoMax = Math.PI / 2.;
        double PhiMin = - Math.PI,     PhiMax = Math.PI;

        double Rho0 = RhoMax - (RhoMax - RhoMin) / (_nbStacks + 2.), 
        	   Rho1 = RhoMin + (RhoMax - RhoMin) / (_nbStacks + 2.), 
        	   dRho = (Rho1 - Rho0) / (_nbStacks);
        double Phi0 = PhiMin,
        	   Phi1 = PhiMax,
        	   dPhi = (Phi1 - Phi0) / (_nbSlices);

        /// Declaration
        declaration	= gx.createDeclaration(new DeclarationElement[] {
        		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
        		new DeclarationElement(1, ElementUsage.Normal,    ElementType.Double3),
        		new DeclarationElement(2, ElementUsage.TexCoord0, ElementType.Double2)
        });

        /// Primitive Type
        primitiveType = PrimitiveType.TriangleList;
        polygonMode   = PolygonMode.Realistic;
    	faceType      = FaceType.FrontAndBack;

		// Build North Pole
		// ----------------
        Point3D[]  northVertex = new Point3D[_nbSlices + 2];
        Vector3D[] northNormal = new Vector3D[_nbSlices + 2];
        Vector2D[] northMap    = new Vector2D[_nbSlices + 2];
        int[]      northIndex  = new int[_nbSlices + 2];

        northVertex[0] = model().getSurfacePoint(Math.PI / 2.0, 0);
		northNormal[0] = Vectors.of(0, 0, 1);
        northMap[0]    = Vectors.of(0.5, 0);
		for(int i = 0; i < _nbSlices + 1; ++i) {
			northVertex[i + 1] = model().getSurfacePoint(Rho0, Phi0 + i * dPhi);
			northNormal[i + 1] = Vectors.of(northVertex[i + 1].normalized());
			
			if(heightMap != null) {
				double height = heightMap.getHeightRadian(Phi0 + i * dPhi, Rho0);
				northVertex[i + 1] = northVertex[i + 1].plus(northNormal[i + 1].times(1 + height));
			}
			
			northMap[i + 1]    = Vectors.of((float) i / (_nbSlices), (float) 1.0 / (_nbStacks + 2));
		}
//		for(int i = 0; i < _nbSlices + 2; ++i)
//			northIndex[i] = i;

        /// Buffer Creation
        northPoleVB   = gx.createVertexBuffer(Buffers3D.asBuffer(northVertex), Primitives.DOUBLE, 3 * (_nbSlices + 2), (BufferFlag[]) null);
//      northPoleNB   = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.FLOAT, 3 * (_n + 2), (BufferFlag[]) null);
        northPoleMB   = gx.createVertexBuffer(Buffers3D.asBuffer(northMap),    Primitives.DOUBLE, 2 * (_nbSlices + 2), (BufferFlag[]) null);
//        northPoleIB   = gx.createIndexBuffer(Buffers3D.asBuffer(northIndex), _nbSlices + 2, (BufferFlag[]) null);

		// Build South Pole
		// ----------------
        Point3D[]  southVertex = new Point3D[_nbSlices + 2];
        Vector3D[] southNormal = new Vector3D[_nbSlices + 2];
        Vector2D[] southMap    = new Vector2D[_nbSlices + 2];
        int[]      southIndex  = new int[_nbSlices + 2];

		southVertex[0] = model().getSurfacePoint(- Math.PI / 2.0, 0);
		southNormal[0] = Vectors.of(0, 0, -1);
        southMap[0]    = Vectors.of(0.5, 1);
		for(int i = 0; i < _nbSlices + 1; ++i) {
			southVertex[i + 1] = model().getSurfacePoint(Rho1, Phi0 + i * dPhi);
			southNormal[i + 1] = Vectors.of(southVertex[i + 1].normalized());

			if(heightMap != null) {
				double height = heightMap.getHeightRadian(Phi0 + i * dPhi, Rho0);
				southVertex[i + 1] = southVertex[i + 1].plus(southNormal[i + 1].times(height));
			}

			southMap[i + 1]    = Vectors.of((float) i / (_nbSlices), (float) (_nbStacks + 1) / (_nbStacks + 2));
		}

//		for(int i = 0; i < _nbSlices + 2; ++i)
//			southIndex[i] = i;
//		southIndex[_nbSlices + 1] = 1;

        /// Buffer Creation
        southPoleVB   = gx.createVertexBuffer(Buffers3D.asBuffer(southVertex), Primitives.DOUBLE, (int) 3 * (_nbSlices + 2), (BufferFlag[]) null);
//      southPoleNB   = gx.createVertexBuffer(Buffers3D.asBuffer(normal_tab), Primitives.FLOAT, (int) 3 * (_n + 2), (BufferFlag[]) null);
        southPoleMB   = gx.createVertexBuffer(Buffers3D.asBuffer(southMap),    Primitives.DOUBLE, (int) 2 * (_nbSlices + 2), (BufferFlag[]) null);
//        southPoleIB   = gx.createIndexBuffer(Buffers3D.asBuffer(southIndex), _nbSlices + 2, (BufferFlag[]) null);


		// Build Main Shape
		// ----------------
        if(_nbStacks > 0) {
			int        nbPoints    = (_nbStacks + 1) * (_nbSlices + 1);
	
	        Point3D[]  coreVertex  = new Point3D[nbPoints];
	        Vector3D[] coreNormal  = new Vector3D[nbPoints];
	        Vector2D[] coreMap     = new Vector2D[nbPoints];
	        int[]      coreIndex   = new int[6 * _nbStacks * _nbSlices];
	
	        double iPhi = 0, jRho = 0;
			for(int j = 0; j < _nbStacks + 1; ++j)
				for(int i = 0; i < _nbSlices + 1; ++i) {
					int index = j * (_nbSlices + 1) + i;
	
					iPhi = Phi0 + i * dPhi;
					jRho = Rho0 + j * dRho;
	
					coreVertex[index] = model().getSurfacePoint(jRho, iPhi);
					coreNormal[index] = Vectors.of(coreVertex[index].normalized());

					if(heightMap != null) {
						double height = heightMap.getHeightRadian(iPhi, jRho);
						coreVertex[index] = coreVertex[index].plus(coreNormal[index].times(height));
					}

					coreMap[index]    = Vectors.of((float) i / (_nbSlices), (float) (j + 1) / (_nbStacks + 2));
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
	
	        /// Buffer Creation
	        planeVB       = gx.createVertexBuffer(Buffers3D.asBuffer(coreVertex), Primitives.DOUBLE, (int) 3 * nbPoints, (BufferFlag[]) null);
	        planeNB       = gx.createVertexBuffer(Buffers3D.asBuffer(coreNormal), Primitives.DOUBLE, (int) 3 * nbPoints, (BufferFlag[]) null);
	        planeMB       = gx.createVertexBuffer(Buffers3D.asBuffer(coreMap),    Primitives.DOUBLE, (int) 2 * nbPoints, (BufferFlag[]) null);
	        planeIB       = gx.createIndexBuffer(Buffers3D.asBuffer(coreIndex), (int) (6 * _nbStacks * _nbSlices), (BufferFlag[]) null);
        }
    }

}
