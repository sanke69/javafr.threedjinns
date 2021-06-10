package fr.threedijnns.objects.space.wrapper;

import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.geometry.space.types.SimpleRay3D;
import fr.threedijnns.gx;

public class GxRay3D extends GxAbstractWrapper3D<SimpleRay3D> {
	double width;

	public GxRay3D(SimpleRay3D _ray) {
		super(_ray);

		width = 1.0;
	}
	public GxRay3D(SimpleRay3D _ray, double _size) {
		super(_ray);

		width = _size;
	}
	public GxRay3D(SimpleRay3D _ray, double _size, int _color) {
		super(_ray);

		isColorEnabled = true;
		color = _color;
		width = _size;
	}

	@Override
	public void renderModel() {
		final Point3D  O = model().getOrigin();
		final Vector3D d = model().getDirection();

		gx.setLineWidth((float) 0.5);
		gx.line(O, O.plus(d.times(1e4)));
		gx.setLineWidth(1.0f);
	}

}
