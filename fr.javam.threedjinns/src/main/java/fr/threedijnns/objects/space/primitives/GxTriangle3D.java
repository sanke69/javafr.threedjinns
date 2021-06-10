package fr.threedijnns.objects.space.primitives;

import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.Points;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.base.GxObject3DBase;

public class GxTriangle3D extends GxObject3DBase {

	Point3D  A, B, C;
	Vector3D N;
	Point2D  Ta, Tb, Tc;

	public GxTriangle3D() {
		super();
		A = Points.of(1, 0, 0);
		B = Points.of(0, 1, 0);
		C = Points.of(0, 0, 1);
	}
	public GxTriangle3D(Point3D _A, Point3D _B, Point3D _C) {
		super();
		A = _A;
		B = _B;
		C = _C;
	}

	@Override
	public void process() {
		;
	}

	@Override
	public void render() {
		preRender();
		
		gx.begin(PrimitiveType.TriangleList);
			gx.vertex(A);
			if(isTextureEnabled() && Ta != null) gx.texCoords(Ta);

			gx.vertex(B);
			if(isTextureEnabled() && Tb != null) gx.texCoords(Tb);

			gx.vertex(C);
			if(isTextureEnabled() && Tc != null) gx.texCoords(Tc);
		gx.end();
		
		postRender();
	}

}
