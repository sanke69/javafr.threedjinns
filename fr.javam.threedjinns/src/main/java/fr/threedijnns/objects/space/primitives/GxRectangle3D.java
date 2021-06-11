package fr.threedijnns.objects.space.primitives;

import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.geometry.types.Points;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.base.GxObject3DBase;

public class GxRectangle3D extends GxObject3DBase implements GxRenderable {

	protected Point3D[]   vertex;
	protected Point2D[]   text;
	
	public GxRectangle3D() {
		super();
		vertex    = new Point3D[4];
		vertex[0] = Points.of(0.0f, 0.0f, 0.0f);
		vertex[1] = Points.of(0.0f, 1.0f, 0.0f);
		vertex[2] = Points.of(1.0f, 1.0f, 0.0f);
		vertex[3] = Points.of(1.0f, 0.0f, 0.0f);

		text      = new Point2D[4];
		text[0]   = Points.of(0.0f, 0.0f);
		text[1]   = Points.of(0.0f, 1.0f);
		text[2]   = Points.of(1.0f, 1.0f);
		text[3]   = Points.of(1.0f, 0.0f);
	}
	public GxRectangle3D(Point3D _bl, Point3D _tl, Point3D _tr, Point3D _br) {
		this();
		vertex    = new Point3D[4];
		vertex[0] = _bl;
		vertex[1] = _tl;
		vertex[2] = _tr;
		vertex[3] = _br;

		text      = new Point2D[4];
		text[0]   = Points.of(0.0f, 0.0f);
		text[1]   = Points.of(0.0f, 1.0f);
		text[2]   = Points.of(1.0f, 1.0f);
		text[3]   = Points.of(1.0f, 0.0f);
	}
	public GxRectangle3D(Point3D[] _bounds) {
		this();
		vertex    = new Point3D[4];
		vertex[0] = _bounds[0];
		vertex[1] = _bounds[1];
		vertex[2] = _bounds[2];
		vertex[3] = _bounds[3];

		text      = new Point2D[4];
		text[0]   = Points.of(0.0f, 0.0f);
		text[1]   = Points.of(0.0f, 1.0f);
		text[2]   = Points.of(1.0f, 1.0f);
		text[3]   = Points.of(1.0f, 0.0f);
	}


	@Override
	public void process() {
//		getFrame().getOrigin().set(-1, 1, 1.5);
//		getFrame().rotateAxes(0, 90, 0);
	}

	@Override
	public void render() {
		gx.disable(EngineOption.GX_LIGHTING);
//		gx.pushMatrix(MatrixType.MAT_MODELVIEW);
//		gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, frame.getModelMatrix());

		if(isTextureEnabled() && hasTexture())
			getTexture().enable(0);

		gx.begin(PrimitiveType.QuadList);
			gx.vertex	(vertex[0]);
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(0.0, 0.0);
//				gx.texCoords	(text[0].getX(), text[0].getY());

			gx.vertex	(vertex[1]);
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(1.0, 0.0);
//				gx.texCoords	(text[1].getX(), text[1].getY());

			gx.vertex	(vertex[2]);
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(1.0, 1.0);
//				gx.texCoords	(text[2].getX(), text[2].getY());

			gx.vertex	(vertex[3]);
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(0.0, 1.0);
//				gx.texCoords	(text[3].getX(), text[3].getY());
		gx.end();

		if(isTextureEnabled() && hasTexture())
			getTexture().disable(0);

//		gx.popMatrix(MatrixType.MAT_MODELVIEW);
		gx.enable(EngineOption.GX_LIGHTING);
	}
}
