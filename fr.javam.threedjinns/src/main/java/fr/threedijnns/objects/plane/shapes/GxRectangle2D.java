package fr.threedijnns.objects.plane.shapes;

import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.plane.Rectangle2D;
import fr.java.maths.geometry.Plane;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.java.maths.geometry.types.Points;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.base.GxObject2DBase;

public class GxRectangle2D extends GxObject2DBase implements GxRenderable {

	protected Rectangle2D rect;
	protected Point2D[]   text;
	
	public GxRectangle2D() {
		super();
		rect = Plane.newRectangle();
		text = new Point2D[4];
		text[0] = Points.of(0.0f, 0.0f);
		text[1] = Points.of(0.0f, 1.0f);
		text[2] = Points.of(1.0f, 1.0f);
		text[3] = Points.of(1.0f, 0.0f);
	}
	public GxRectangle2D(float _x, float _y, float _w, float _h) {
		this();
		rect = Plane.newRectangle(_x, _y, _w, _h);
	}
	public GxRectangle2D(Rectangle2D _rect) {
		this();
		rect = Plane.newRectangle(_rect);
	}

	@Override
	public void process() {
		;
	}

	@Override
	public void render() {
		if(isTextureEnabled() && hasTexture())
			getTexture().enable(0);

		gx.begin(PrimitiveType.QuadList);
			gx.vertex	(rect.getMaxX(), rect.getMinY());
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(text[0].getX(), text[0].getY());

			gx.vertex	(rect.getMinX(), rect.getMinY());
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(text[1].getX(), text[1].getY());

			gx.vertex	(rect.getMinX(), rect.getMaxY());
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(text[2].getX(), text[2].getY());

			gx.vertex	(rect.getMaxX(), rect.getMaxY());
			if(isTextureEnabled() && hasTexture())
				gx.texCoords	(text[3].getX(), text[3].getY());
		gx.end();

		if(isTextureEnabled() && hasTexture())
			getTexture().disable(0);
	}
}
