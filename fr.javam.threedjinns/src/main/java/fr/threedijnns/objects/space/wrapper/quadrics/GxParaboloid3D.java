package fr.threedijnns.objects.space.wrapper.quadrics;

import fr.java.maths.geometry.space.shapes.quadrics.Quadric3D.COEF;
import fr.java.maths.geometry.space.shapes.quadrics.surfaces.Paraboloid3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.space.wrapper.GxAbstractWrapper3D;

public class GxParaboloid3D extends GxAbstractWrapper3D<Paraboloid3D> {

	public GxParaboloid3D(Paraboloid3D _model) {
		super(_model);
	}

	@Override
	public void renderModel() {
		int n = 100, m = 100;

		gx.begin(PrimitiveType.QuadList);
		if(model().getCoef(COEF.X) != 0)
			for(int i = -n; i < n; ++i) {
				for(int j = -m; j < m; ++j) {
					gx.vertex(model().getSurfacePointX(i,   j),   i,   j);
					gx.vertex(model().getSurfacePointX(i+1, j),   i+1, j);	
					gx.vertex(model().getSurfacePointX(i+1, j+1), i+1, j+1); 	
					gx.vertex(model().getSurfacePointX(i,   j+1), i,   j+1);
				}
			}
		else if(model().getCoef(COEF.Y) != 0)
			for(int i = -n; i < n; ++i) {
				for(int j = -m; j < m; ++j) {
					gx.vertex(i,   model().getSurfacePointY(i,   j)  , j);
					gx.vertex(i+1, model().getSurfacePointY(i+1, j)  , j);
					gx.vertex(i+1, model().getSurfacePointY(i+1, j+1), j+1);
					gx.vertex(i,   model().getSurfacePointY(i,   j+1), j+1);
				}
			}
		else if(model().getCoef(COEF.Z) != 0)
			for(int i = -n; i < n; ++i) {
				for(int j = -m; j < m; ++j) {
					gx.vertex(i,	j, 		model().getSurfacePointZ(i,   j));
					gx.vertex(i+1,	j, 		model().getSurfacePointZ(i+1, j));
					gx.vertex(i+1,	j+1, 	model().getSurfacePointZ(i+1, j+1));
					gx.vertex(i,	j+1, 	model().getSurfacePointZ(i,   j+1));
				}
			}
		else
			throw new IllegalArgumentException();
		gx.end();

	}
/*
	@Override
	public void renderModel() {
		int    n   = 100, m     = 100;

		Point3D  oRay = Point3D.zero().plus(model().getNormal().normalized().times(100.0));
		Vector3D dRay = model().getNormal().normalized().negate();

		double A = model().getCoef(COEF.X);
		double B = model().getCoef(COEF.Y);
		double C = model().getCoef(COEF.Z);

		Point3D   O  = model().getSurfacePointZ(0, 0);
		Vector3D  V0 = Vector3D.of(C, C, -A-B);
		Vector3D  V1 = Vector3D.of(B, -A-C, B);
		Vector3D  V2 = Vector3D.of(-B-C, A, A);

		gx.begin(PrimitiveType.TriangleList);
		for(int i = 0; i < n; ++i) {
			for(int j = 0; j < n; ++j) {
				gx.vertex(left,		bottom, 	front);
				gx.vertex(right,	bottom, 	front);
				gx.vertex(right,	top, 		front);
				
			}
		}
		gx.end();

	}
*/
}
